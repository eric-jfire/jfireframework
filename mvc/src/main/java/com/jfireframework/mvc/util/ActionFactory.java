package com.jfireframework.mvc.util;

import java.lang.reflect.Method;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.context.bean.Bean;
import com.jfireframework.mvc.annotation.ActionMethod;
import com.jfireframework.mvc.annotation.ContentType;
import com.jfireframework.mvc.binder.DataBinderFactory;
import com.jfireframework.mvc.core.Action;
import com.jfireframework.mvc.rest.RestfulUrlTool;

public class ActionFactory
{
    
    /**
     * 使用方法对象，顶级请求路径，容器对象初始化一个action实例。
     * 该实例负责该action的调用
     * 
     * @param method
     * @param rootRequestPath 顶级请求路径，实际的请求路径为顶级请求路径/方法请求路径
     * @param beanContext
     */
    public static Action buildAction(Method method, String requestPath, Bean bean, BeetlRender beetlRender, Class<?> type)
    {
        Action action = new Action(method);
        ActionMethod actionMethod = method.getAnnotation(ActionMethod.class);
        Verify.True(actionMethod.methods().length > 0, "action的允可方法列表为空，请检查{}.{}", method.getDeclaringClass().getName(), method.getName());
        action.setRequestMethods(actionMethod.methods());
        if (actionMethod.url().equals("/"))
        {
        }
        else
        {
            requestPath += "/" + (StringUtil.isNotBlank(actionMethod.url()) ? actionMethod.url() : method.getName());
        }
        if (requestPath.contains("{"))
        {
            action.setRest(true);
            action.setRestfulRule(RestfulUrlTool.build(requestPath, action));
        }
        action.setDataBinders(DataBinderFactory.build(method));
        action.setReadStream(actionMethod.readStream());
        action.setRequestUrl(requestPath);
        action.setActionEntity(bean.getInstance());
        action.setResultType(actionMethod.resultType(), beetlRender);
        if (actionMethod.contentType().equals(ContentType.SELFADAPTION) == false)
        {
            action.setContentType(actionMethod.contentType());
        }
        try
        {
            // 使用原始方法的名称和参数类型数组。就算是增强后的子类，该名称和参数类型数组信息也是不变的。
            Method realMethod = type.getMethod(method.getName(), method.getParameterTypes());
            action.setMethodAccessor(ReflectUtil.fastMethod(realMethod));
        }
        catch (NoSuchMethodException | SecurityException e)
        {
            throw new RuntimeException(e);
        }
        return action;
    }
    
}
