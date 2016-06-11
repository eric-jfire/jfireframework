package com.jfireframework.mvc.util;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import com.jfireframework.baseutil.collection.set.LightSet;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.reflect.SimpleHotswapClassLoader;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.codejson.JsonObject;
import com.jfireframework.context.JfireContext;
import com.jfireframework.context.JfireContextImpl;
import com.jfireframework.context.aop.AopUtil;
import com.jfireframework.context.bean.Bean;
import com.jfireframework.context.util.AnnotationUtil;
import com.jfireframework.mvc.annotation.Controller;
import com.jfireframework.mvc.annotation.RequestMapping;
import com.jfireframework.mvc.core.Action;
import com.jfireframework.mvc.core.ActionCenter;
import com.jfireframework.mvc.core.ActionInitListener;
import com.jfireframework.mvc.interceptor.impl.DataBinderInterceptor;
import com.jfireframework.mvc.interceptor.impl.UploadInterceptor;
import com.jfireframework.mvc.viewrender.RenderFactory;

public class ActionCenterBulder
{
    
    public static ActionCenter generate(JsonObject config, ServletContext servletContext, String encode)
    {
        boolean devMode = config.containsKey("devMode") ? config.getBoolean("devMode") : false;
        JfireContext jfireContext = new JfireContextImpl();
        if (devMode)
        {
            Verify.True(config.contains("reloadPackage"), "开发模式为true，此时应该配置reloadPackage内容");
            Verify.True(config.contains("reloadPath"), "开发模式为true，此时应该配置monitorPath内容");
            String reloadPackage = config.getWString("reloadPackage");
            String reloadPath = config.getWString("reloadPath");
            SimpleHotswapClassLoader classLoader = new SimpleHotswapClassLoader(reloadPath, reloadPackage);
            jfireContext.addSingletonEntity(classLoader.getClass().getName(), classLoader);
            jfireContext.setClassLoader(classLoader);
        }
        AopUtil.initClassPool();
        jfireContext.readConfig(config);
        jfireContext.addPackageNames("com.jfireframework.sql");
        jfireContext.addSingletonEntity("servletContext", servletContext);
        jfireContext.addSingletonEntity(BeetlKit.class.getName(), new BeetlKit());
        jfireContext.addBean(DataBinderInterceptor.class);
        jfireContext.addBean(UploadInterceptor.class);
        boolean report = config.getWBoolean("report") == null ? false : config.getBoolean("report");
        if (report)
        {
            jfireContext.addBean(ReportMdActionListener.class);
        }
        return new ActionCenter(generateActions(servletContext.getContextPath(), jfireContext, new RenderFactory(Charset.forName(encode))).toArray(new Action[0]));
    }
    
    /**
     * 初始化Beancontext容器，并且抽取其中的ActionClass注解的类，将action实例化
     */
    private static List<Action> generateActions(String contextUrl, JfireContext jfireContext, RenderFactory renderFactory)
    {
        Bean[] beans = jfireContext.getBeanByAnnotation(Controller.class);
        Bean[] listenerBeans = jfireContext.getBeanByInterface(ActionInitListener.class);
        LightSet<ActionInitListener> tmp = new LightSet<ActionInitListener>();
        for (Bean each : listenerBeans)
        {
            tmp.add((ActionInitListener) each.getInstance());
        }
        ActionInitListener[] listeners = tmp.toArray(ActionInitListener.class);
        List<Action> list = new ArrayList<Action>();
        for (Bean each : beans)
        {
            list.addAll(generateActions(each, listeners, jfireContext, contextUrl, renderFactory));
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
    private static List<Action> generateActions(Bean bean, ActionInitListener[] listeners, JfireContext jfireContext, String contextUrl, RenderFactory renderFactory)
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
                Action action = ActionFactory.buildAction(each, requestUrl, bean, jfireContext, renderFactory);
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
