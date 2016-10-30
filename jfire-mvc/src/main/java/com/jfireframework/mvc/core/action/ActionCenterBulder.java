package com.jfireframework.mvc.core.action;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.ServletContext;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.reflect.SimpleHotswapClassLoader;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.codejson.JsonObject;
import com.jfireframework.codejson.JsonTool;
import com.jfireframework.context.JfireContext;
import com.jfireframework.context.JfireContextImpl;
import com.jfireframework.context.aliasanno.AnnotationUtil;
import com.jfireframework.context.aop.AopUtil;
import com.jfireframework.context.bean.Bean;
import com.jfireframework.mvc.annotation.Controller;
import com.jfireframework.mvc.annotation.RequestMapping;
import com.jfireframework.mvc.interceptor.impl.DataBinderInterceptor;
import com.jfireframework.mvc.interceptor.impl.UploadInterceptor;
import com.jfireframework.mvc.util.ClasspathBeetlKit;
import com.jfireframework.mvc.util.WebAppBeetlKit;
import com.jfireframework.mvc.viewrender.impl.BeetlRender;
import com.jfireframework.mvc.viewrender.impl.BytesRender;
import com.jfireframework.mvc.viewrender.impl.HtmlRender;
import com.jfireframework.mvc.viewrender.impl.JsonRender;
import com.jfireframework.mvc.viewrender.impl.JspRender;
import com.jfireframework.mvc.viewrender.impl.NoneRender;
import com.jfireframework.mvc.viewrender.impl.RedirectRender;
import com.jfireframework.mvc.viewrender.impl.StringRender;

public class ActionCenterBulder
{
    
    public static ActionCenter generate(JsonObject config, ServletContext servletContext, String encode)
    {
        boolean devMode = config.containsKey("devMode") ? config.getBoolean("devMode") : false;
        JfireContext jfireContext = new JfireContextImpl();
        ClassLoader classLoader;
        if (devMode)
        {
            Verify.True(config.contains("reloadPackage"), "开发模式为true，此时应该配置reloadPackage内容");
            Verify.True(config.contains("reloadPath"), "开发模式为true，此时应该配置monitorPath内容");
            String reloadPackage = config.getWString("reloadPackage");
            String excludePackage = config.getWString("excludePackage");
            String reloadPath = config.getWString("reloadPath");
            classLoader = new SimpleHotswapClassLoader(reloadPath);
            ((SimpleHotswapClassLoader) classLoader).setReloadPackages(reloadPackage.split(","));
            if (excludePackage != null)
            {
                ((SimpleHotswapClassLoader) classLoader).setExcludePackages(excludePackage.split(","));
            }
            jfireContext.addSingletonEntity(classLoader.getClass().getName(), classLoader);
            jfireContext.setClassLoader(classLoader);
            AopUtil.initClassPool(classLoader);
            JsonTool.initClassPool(classLoader);
        }
        else
        {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        jfireContext.addSingletonEntity("charset", Charset.forName(encode));
        addViewRender(jfireContext);
        jfireContext.readConfig(config);
        jfireContext.addSingletonEntity("servletContext", servletContext);
        jfireContext.addBean(DataBinderInterceptor.class);
        jfireContext.addBean(UploadInterceptor.class);
        return new ActionCenter(generateActions(servletContext.getContextPath(), jfireContext).toArray(new Action[0]));
    }
    
    private static void addViewRender(JfireContext jfireContext)
    {
        jfireContext.addBean(WebAppBeetlKit.class);
        jfireContext.addBean(ClasspathBeetlKit.class);
        jfireContext.addBean(BeetlRender.class);
        jfireContext.addBean(JspRender.class);
        jfireContext.addBean(JsonRender.class);
        jfireContext.addBean(HtmlRender.class);
        jfireContext.addBean(StringRender.class);
        jfireContext.addBean(RedirectRender.class);
        jfireContext.addBean(NoneRender.class);
        jfireContext.addBean(BytesRender.class);
    }
    
    /**
     * 初始化Beancontext容器，并且抽取其中的ActionClass注解的类，将action实例化
     */
    private static List<Action> generateActions(String contextUrl, JfireContext jfireContext)
    {
        Bean[] beans = jfireContext.getBeanByAnnotation(Controller.class);
        Bean[] listenerBeans = jfireContext.getBeanByInterface(ActionInitListener.class);
        List<ActionInitListener> tmp = new LinkedList<ActionInitListener>();
        for (Bean each : listenerBeans)
        {
            tmp.add((ActionInitListener) each.getInstance());
        }
        ActionInitListener[] listeners = tmp.toArray(new ActionInitListener[tmp.size()]);
        List<Action> list = new ArrayList<Action>();
        for (Bean each : beans)
        {
            list.addAll(generateActions(each, listeners, jfireContext, contextUrl));
        }
        return list;
    }
    
    /**
     * 创建某一个bean下面的所有action
     * 
     * @param bean
     * @param listeners
     * @param contextUrl
     * @param jfireContext
     * @return
     */
    private static List<Action> generateActions(Bean bean, ActionInitListener[] listeners, JfireContext jfireContext, String contextUrl)
    {
        Class<?> src = bean.getOriginType();
        String requestUrl = contextUrl;
        if (AnnotationUtil.isPresent(RequestMapping.class, src))
        {
            RequestMapping requestMapping = AnnotationUtil.getAnnotation(RequestMapping.class, src);
            requestUrl += requestMapping.value();
        }
        // 这里需要使用原始的类来得到方法，因为如果使用增强后的子类，就无法得到正确的方法名称以及方法上的注解信息
        Method[] methods = ReflectUtil.getAllMehtods(bean.getOriginType());
        List<Action> list = new ArrayList<Action>();
        for (Method each : methods)
        {
            if (AnnotationUtil.isPresent(RequestMapping.class, each))
            {
                Action action = ActionFactory.buildAction(each, requestUrl, bean, jfireContext);
                list.add(action);
                for (ActionInitListener listener : listeners)
                {
                    listener.init(action);
                }
            }
        }
        return list;
    }
}
