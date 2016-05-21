package com.jfireframework.mvc.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.order.AescComparator;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.context.JfireContext;
import com.jfireframework.context.bean.Bean;
import com.jfireframework.mvc.annotation.ActionMethod;
import com.jfireframework.mvc.binder.DataBinder;
import com.jfireframework.mvc.binder.DataBinderFactory;
import com.jfireframework.mvc.core.Action;
import com.jfireframework.mvc.interceptor.ActionInterceptor;
import com.jfireframework.mvc.rest.RestfulUrlTool;

public class ActionFactory
{
    
    private static final AescComparator AESC_COMPARATOR = new AescComparator();
    
    /**
     * 使用方法对象，顶级请求路径，容器对象初始化一个action实例。 该实例负责该action的调用
     * 
     * @param method
     * @param rootRequestPath 顶级请求路径，实际的请求路径为顶级请求路径/方法请求路径
     * @param beanContext
     */
    public static Action buildAction(Method method, String requestPath, Bean bean, JfireContext jfireContext)
    {
        ActionInfo actionInfo = new ActionInfo();
        actionInfo.setMethod(method);
        ActionMethod actionMethod = method.getAnnotation(ActionMethod.class);
        Verify.True(actionMethod.methods().length > 0, "action的允可方法列表为空，请检查{}.{}", method.getDeclaringClass().getName(), method.getName());
        actionInfo.setRequestMethods(actionMethod.methods());
        actionInfo.setDataBinders(DataBinderFactory.build(method));
        actionInfo.setReadStream(actionMethod.readStream());
        actionInfo.setEntity(bean.getInstance());
        actionInfo.setResultType(actionMethod.resultType());
        actionInfo.setContentType(actionMethod.contentType());
        if (actionMethod.url().equals("/"))
        {
            ;
        }
        else
        {
            actionInfo.setRest(actionMethod.rest());
            if (actionMethod.rest())
            {
                if (StringUtil.isNotBlank(actionMethod.url()))
                {
                    requestPath += "/" + actionMethod.url();
                }
                else
                {
                    requestPath += "/" + method.getName();
                }
                if (requestPath.indexOf("{") == -1)
                {
                    for (DataBinder each : actionInfo.getDataBinders())
                    {
                        requestPath += "/{" + each.getParamName() + "}";
                    }
                }
                actionInfo.setRestfulRule(RestfulUrlTool.build(requestPath));
            }
            else
            {
                requestPath += "/" + (StringUtil.isNotBlank(actionMethod.url()) ? actionMethod.url() : method.getName());
            }
        }
        actionInfo.setRequestUrl(requestPath);
        try
        {
            // 使用原始方法的名称和参数类型数组,在类型中获取真实的方法。这一步主要是防止action类本身被增强后，却仍然调用未增强的方法。
            Method realMethod = bean.getType().getMethod(method.getName(), method.getParameterTypes());
            actionInfo.setMethodAccessor(ReflectUtil.fastMethod(realMethod));
        }
        catch (NoSuchMethodException | SecurityException e)
        {
            throw new JustThrowException(e);
        }
        Bean[] beans = jfireContext.getBeanByInterface(ActionInterceptor.class);
        List<ActionInterceptor> interceptors = new ArrayList<>();
        for (Bean each : beans)
        {
            ActionInterceptor interceptor = (ActionInterceptor) each.getInstance();
            String rule = interceptor.rule();
            if ("*".equals(rule))
            {
                interceptors.add(interceptor);
            }
            else
            {
                for (String singleRule : rule.split(";"))
                {
                    if (requestPath.startsWith(singleRule))
                    {
                        interceptors.add(interceptor);
                        break;
                    }
                }
            }
        }
        interceptors.sort(AESC_COMPARATOR);
        actionInfo.setInterceptors(interceptors.toArray(new ActionInterceptor[interceptors.size()]));
        return new Action(actionInfo);
    }
}
