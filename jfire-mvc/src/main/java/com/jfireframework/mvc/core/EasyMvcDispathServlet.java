package com.jfireframework.mvc.core;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.resource.WebAppResourceLoader;
import com.jfireframework.baseutil.collection.set.LightSet;
import com.jfireframework.baseutil.exception.JustThrowException;
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
import com.jfireframework.mvc.interceptor.ActionInterceptor;
import com.jfireframework.mvc.interceptor.impl.DataBinderInterceptor;
import com.jfireframework.mvc.interceptor.impl.UploadInterceptor;
import com.jfireframework.mvc.util.ActionFactory;
import com.jfireframework.mvc.util.BeetlRender;
import com.jfireframework.mvc.util.HotwrapClassLoader;
import com.jfireframework.mvc.view.BeetlView;
import com.jfireframework.mvc.view.BytesView;
import com.jfireframework.mvc.view.HtmlView;
import com.jfireframework.mvc.view.JsonView;
import com.jfireframework.mvc.view.JspView;
import com.jfireframework.mvc.view.NoneView;
import com.jfireframework.mvc.view.RedirectView;
import com.jfireframework.mvc.view.StringView;

/**
 * 充当路径分发器的类，用来根据地址规则转发数据请求
 * 
 * @author 林斌（eric@jfire.cn）
 * 
 */
@WebServlet(name = "EasyMvcDispathServlet", value = "/*", loadOnStartup = 1, asyncSupported = true)
@MultipartConfig
public class EasyMvcDispathServlet extends HttpServlet
{
    /**
     * 
     */
    private static final long serialVersionUID = 6091581255799463902L;
    private Logger            logger           = ConsoleLogFactory.getLogger();
    private JfireContext      jfireContext;
    private ActionCenter      actionCenter;
    private String            contextUrl;
    private ServletContext    servletContext;
    private RequestDispatcher staticResourceDispatcher;
    private File              configFile;
    private File              monitorFile;
    private String[]          reloadPackages;
    private WatchService      watcher;
    private boolean           devMode          = false;
    private BeetlView         beetlView;
    private BytesView         bytesView;
    private JsonView          JsonView;
    private StringView        stringView;
    private HtmlView          htmlView;
    private JspView           jspView;
    private RedirectView      redirectView;
    private NoneView          noneView;
    
    @Override
    public void init(ServletConfig servletConfig) throws ServletException
    {
        logger.debug("初始化Context-mvc Servlet");
        servletContext = servletConfig.getServletContext();
        staticResourceDispatcher = getStaticResourceDispatcher();
        contextUrl = servletContext.getContextPath();
        try
        {
            configFile = new File(this.getClass().getClassLoader().getResource("mvc.json").toURI());
        }
        catch (Exception e)
        {
            throw new UnSupportException("解析配置文件异常", e);
        }
        if (servletConfig.getInitParameter("debug") != null && servletConfig.getInitParameter("debug").equals("true"))
        {
            logger.warn("以debug模式启动服务器");
            devMode = true;
            String monitorPath = servletConfig.getInitParameter("monitorPath");
            reloadPackages = servletConfig.getInitParameter("reloadPackages").split(",");
            monitorFile = new File(monitorPath);
            initWatcher(monitorFile);
        }
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
        jfireContext = new JfireContextImpl();
        initMvc();
    }
    
    /**
     * 初始化mvc需要的所有的类和实例
     */
    private void initMvc()
    {
        AopUtil.initClassPool();
        jfireContext.readConfig(configFile);
        jfireContext.addPackageNames("com.jfireframework.sql");
        jfireContext.addSingletonEntity("servletContext", servletContext);
        addDefaultInterceptors();
        actionCenter = new ActionCenter(initUrlActionMap(contextUrl, jfireContext).toArray(new Action[0]));
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
    
    private void addDefaultInterceptors()
    {
        jfireContext.addBean(DataBinderInterceptor.class);
        jfireContext.addBean(UploadInterceptor.class);
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
    
    private void initWatcher(File monitorDir)
    {
        Set<File> dirs = new HashSet<>();
        getChildDirs(monitorDir, dirs);
        Set<Path> paths = new HashSet<>();
        for (File each : dirs)
        {
            paths.add(Paths.get(each.getAbsolutePath()));
        }
        try
        {
            watcher = FileSystems.getDefault().newWatchService();
            for (Path each : paths)
            {
                each.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
    }
    
    private void getChildDirs(File parentDir, Set<File> dirSets)
    {
        dirSets.add(parentDir);
        for (File each : parentDir.listFiles())
        {
            if (each.isDirectory())
            {
                getChildDirs(each, dirSets);
            }
        }
    }
    
    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException
    {
        if (devMode)
        {
            while (true)
            {
                WatchKey key = watcher.poll();
                if (key == null)
                {
                    break;
                }
                for (WatchEvent<?> event : key.pollEvents())
                {
                    Kind<?> kind = event.kind();
                    if (kind == StandardWatchEventKinds.OVERFLOW)
                    {// 事件可能lost or discarded
                        continue;
                    }
                    try
                    {
                        long t0 = System.currentTimeMillis();
                        ClassLoader classLoader = new HotwrapClassLoader(monitorFile, reloadPackages);
                        jfireContext = (JfireContext) classLoader.loadClass("com.jfireframework.context.JfireContextImpl").newInstance();
                        jfireContext.addSingletonEntity(ClassLoader.class.getSimpleName(), classLoader);
                        jfireContext.setClassLoader(classLoader);
                        initMvc();
                        logger.debug("热部署,耗时:{}", System.currentTimeMillis() - t0);
                        if (!key.reset())
                        {
                            break;
                        }
                        break;
                    }
                    catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
                    {
                        if (!key.reset())
                        {
                            break;
                        }
                        throw new JustThrowException(e);
                    }
                }
            }
        }
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        request.setCharacterEncoding(MvcStaticConfig.encode);
        response.setCharacterEncoding(MvcStaticConfig.encode);
        Action action = actionCenter.getAction(request);
        if (action == null)
        {
            staticResourceDispatcher.forward(request, response);
            return;
        }
        try
        {
            for (ActionInterceptor each : action.getInterceptors())
            {
                if (each.interceptor(request, response, action) == false)
                {
                    logger.debug("发生异常{}" + each.getClass().getName());
                    return;
                }
            }
            response.setContentType(action.getContentType());
            Object result = action.invoke((Object[]) request.getAttribute(DataBinderInterceptor.DATABINDERKEY));
            switch (action.getResultType())
            {
                case Beetl:
                    beetlView.render(request, response, result);
                    break;
                case Bytes:
                    bytesView.render(request, response, result);
                    break;
                case Html:
                    htmlView.render(request, response, result);
                    break;
                case Json:
                    JsonView.render(request, response, result);
                    break;
                case Jsp:
                    jspView.render(request, response, result);
                    break;
                case None:
                    noneView.render(request, response, result);
                    break;
                case Redirect:
                    redirectView.render(request, response, result);
                    break;
                case String:
                    stringView.render(request, response, result);
                    break;
                default:
                    throw new UnSupportException("不应该走到这个分支");
            }
        }
        catch (Throwable e)
        {
            logger.error("访问action出现异常,action为{}", action.getRequestUrl(), e);
            response.sendError(500, e.getLocalizedMessage());
        }
    }
}
