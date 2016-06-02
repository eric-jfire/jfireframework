package com.jfireframework.mvc.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import com.jfireframework.baseutil.collection.set.LightSet;
import com.jfireframework.baseutil.reflect.HotswapClassLoader;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.codejson.JsonArray;
import com.jfireframework.codejson.JsonObject;
import com.jfireframework.context.JfireContext;
import com.jfireframework.context.JfireContextImpl;
import com.jfireframework.context.aop.AopUtil;
import com.jfireframework.context.bean.Bean;
import com.jfireframework.mvc.annotation.Controller;
import com.jfireframework.mvc.annotation.RequestMapping;
import com.jfireframework.mvc.config.ResultType;
import com.jfireframework.mvc.core.Action;
import com.jfireframework.mvc.core.ActionCenter;
import com.jfireframework.mvc.core.ActionInitListener;
import com.jfireframework.mvc.interceptor.impl.DataBinderInterceptor;
import com.jfireframework.mvc.interceptor.impl.UploadInterceptor;
import com.jfireframework.mvc.viewrender.RenderFactory;

public class ActionCenterBulder
{
    public static ActionCenter generate(JsonObject config, ServletContext servletContext, RenderFactory renderFactory)
    {
        boolean devMode = config.containsKey("devMode") ? config.getBoolean("devMode") : false;
        JfireContext jfireContext = new JfireContextImpl();
        if (devMode)
        {
            Verify.True(config.contains("reloadPackages"), "开发模式为true，此时应该配置reloadPackages内容");
            Verify.True(config.contains("reloadPaths"), "开发模式为true，此时应该配置monitorPath内容");
            JsonArray jsonArray = config.getJsonArray("reloadPackages");
            String[] reloadPackages = jsonArray.toArray(new String[jsonArray.size()]);
            String[] reloadPaths = config.getJsonArray("reloadPaths").toArray(new String[0]);
            HotswapClassLoader classLoader = new HotswapClassLoader();
            classLoader.setReloadPackages(reloadPackages);
            classLoader.setReloadPaths(reloadPaths);
            jfireContext.setClassLoader(classLoader);
        }
        AopUtil.initClassPool();
        jfireContext.readConfig(config);
        jfireContext.addPackageNames("com.jfireframework.sql");
        jfireContext.addSingletonEntity("servletContext", servletContext);
        jfireContext.addSingletonEntity("beetlRender", renderFactory.getViewRender(ResultType.Beetl));
        jfireContext.addBean(DataBinderInterceptor.class);
        jfireContext.addBean(UploadInterceptor.class);
        boolean report = config.getWBoolean("report") == null ? false : config.getBoolean("report");
        if (report)
        {
            jfireContext.addBean(ReportMdActionListener.class);
        }
        return new ActionCenter(generateActions(servletContext.getContextPath(), jfireContext).toArray(new Action[0]));
    }
    
    /**
     * 初始化Beancontext容器，并且抽取其中的ActionClass注解的类，将action实例化
     */
    private static List<Action> generateActions(String contextUrl, JfireContext jfireContext)
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
        Controller actionClass = src.getAnnotation(Controller.class);
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
        List<Action> list = new ArrayList<Action>();
        for (Method each : methods)
        {
            if (each.isAnnotationPresent(RequestMapping.class))
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
}
