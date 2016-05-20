package com.jfireframework.mvc.core;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.resource.WebAppResourceLoader;
import com.jfireframework.baseutil.collection.set.LightSet;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.context.JfireContext;
import com.jfireframework.context.JfireContextImpl;
import com.jfireframework.context.aop.AopUtil;
import com.jfireframework.context.bean.Bean;
import com.jfireframework.mvc.annotation.ActionClass;
import com.jfireframework.mvc.annotation.ActionMethod;
import com.jfireframework.mvc.config.MvcStaticConfig;
import com.jfireframework.mvc.interceptor.impl.DataBinderInterceptor;
import com.jfireframework.mvc.interceptor.impl.UploadInterceptor;
import com.jfireframework.mvc.util.ActionFactory;
import com.jfireframework.mvc.util.BeetlRender;
import com.jfireframework.mvc.view.BeetlView;
import com.jfireframework.mvc.view.BytesView;
import com.jfireframework.mvc.view.HtmlView;
import com.jfireframework.mvc.view.JsonView;
import com.jfireframework.mvc.view.JspView;
import com.jfireframework.mvc.view.NoneView;
import com.jfireframework.mvc.view.RedirectView;
import com.jfireframework.mvc.view.StringView;

public class DispathServletHelper
{
    private Logger            logger  = ConsoleLogFactory.getLogger();
    private JfireContext      jfireContext;
    private ActionCenter      actionCenter;
    private String            contextUrl;
    private ServletContext    servletContext;
    private RequestDispatcher staticResourceDispatcher;
    private File              configFile;
    private File              monitorFile;
    private String[]          reloadPackages;
    private WatchService      watcher;
    private boolean           devMode = false;
    private BeetlView         beetlView;
    private BytesView         bytesView;
    private JsonView          JsonView;
    private StringView        stringView;
    private HtmlView          htmlView;
    private JspView           jspView;
    private RedirectView      redirectView;
    private NoneView          noneView;
    
    public DispathServletHelper(ServletContext servletContext, ServletConfig servletConfig)
    {
        this.servletContext = servletContext;
        staticResourceDispatcher = getStaticResourceDispatcher();
        Charset charset;
        if (servletConfig.getInitParameter("encode") != null)
        {
            charset = Charset.forName(servletConfig.getInitParameter("encode"));
        }
        else
        {
            charset = Charset.forName("utf8");
        }
        initTemplate(charset);
        try
        {
            File configFile = new File(this.getClass().getClassLoader().getResource("mvc.json").toURI());
            initMvc(configFile);
        }
        catch (Exception e)
        {
            throw new UnSupportException("解析配置文件异常", e);
        }
    }
    
    private void initMvc(File configFile)
    {
        AopUtil.initClassPool();
        jfireContext.readConfig(configFile);
        jfireContext.addPackageNames("com.jfireframework.sql");
        jfireContext.addSingletonEntity("servletContext", servletContext);
        addDefaultInterceptors(jfireContext);
        actionCenter = new ActionCenter(initUrlActionMap(contextUrl, jfireContext).toArray(new Action[0]));
    }
    
    
    /**
     * 初始化Beancontext容器，并且抽取其中的ActionClass注解的类，将action实例化
     */
    private List<Action> initUrlActionMap(String contextUrl, JfireContext jfireContext)
    {
        Bean[] beans = jfireContext.getBeanByAnnotation(ActionClass.class);
        Bean[] listenerBeans = jfireContext.getBeanByInterface(ActionInitListener.class);
        LightSet<ActionInitListener> tmp = new LightSet<>();
        for (Bean each : listenerBeans)
        {
            tmp.add((ActionInitListener) each.getInstance());
        }
        ActionInitListener[] listeners = tmp.toArray(ActionInitListener.class);
        List<Action> list = new ArrayList<>();
        for (Bean each : beans)
        {
            list.addAll(initAction(each, listeners, contextUrl, jfireContext));
        }
        return list;
    }
    
    private List<Action> initAction(Bean bean, ActionInitListener[] listeners, String contextUrl, JfireContext jfireContext)
    {
        Class<?> src = bean.getOriginType();
        ActionClass actionClass = src.getAnnotation(ActionClass.class);
        String modelUrl = null;
        if (actionClass.value().equals("/"))
        {
            modelUrl = contextUrl;
        }
        else
        {
            modelUrl = actionClass.value().equals("") ? '/' + src.getSimpleName() : '/' + actionClass.value();
            modelUrl = contextUrl + modelUrl;
        }
        Verify.False(modelUrl.contains("*"), "顶级url不能包含*");
        // 这里需要使用原始的类来得到方法，因为如果使用增强后的子类，就无法得到正确的方法名称以及方法上的注解信息
        Method[] methods = ReflectUtil.getAllMehtods(bean.getOriginType());
        List<Action> list = new ArrayList<>();
        for (Method each : methods)
        {
            if (each.isAnnotationPresent(ActionMethod.class))
            {
                Action action = ActionFactory.buildAction(each, modelUrl, bean, jfireContext);
                list.add(action);
                for (ActionInitListener listener : listeners)
                {
                    listener.init(action);
                }
            }
        }
        return list;
    }
    
    
    
    private void addDefaultInterceptors(JfireContext jfireContext)
    {
        jfireContext.addBean(DataBinderInterceptor.class);
        jfireContext.addBean(UploadInterceptor.class);
    }
    
    private RequestDispatcher getStaticResourceDispatcher()
    {
        RequestDispatcher requestDispatcher = null;
        if ((requestDispatcher = servletContext.getNamedDispatcher(MvcStaticConfig.COMMON_DEFAULT_SERVLET_NAME)) != null)
        {
        }
        else if ((requestDispatcher = servletContext.getNamedDispatcher(MvcStaticConfig.RESIN_DEFAULT_SERVLET_NAME)) != null)
        {
        }
        else if ((requestDispatcher = servletContext.getNamedDispatcher(MvcStaticConfig.WEBLOGIC_DEFAULT_SERVLET_NAME)) != null)
        {
        }
        else if ((requestDispatcher = servletContext.getNamedDispatcher(MvcStaticConfig.WEBSPHERE_DEFAULT_SERVLET_NAME)) != null)
        {
        }
        else
        {
            throw new RuntimeException("找不到默认用来处理静态资源的处理器");
        }
        return requestDispatcher;
    }
    
    private void initTemplate(Charset charset)
    {
        BeetlRender beetlRender = initBeetlTemplate();
        beetlView = new BeetlView(beetlRender);
        bytesView = new BytesView();
        htmlView = new HtmlView();
        JsonView = new JsonView(charset);
        jspView = new JspView();
        noneView = new NoneView();
        redirectView = new RedirectView();
        stringView = new StringView(charset);
        
    }
    
    private BeetlRender initBeetlTemplate()
    {
        WebAppResourceLoader loader = new WebAppResourceLoader();
        Configuration configuration = null;
        try
        {
            configuration = Configuration.defaultConfiguration();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return new BeetlRender(new GroupTemplate(loader, configuration));
    }
    
}
