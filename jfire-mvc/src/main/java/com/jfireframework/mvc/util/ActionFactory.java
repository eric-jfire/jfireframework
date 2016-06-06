package com.jfireframework.mvc.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.order.AescComparator;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.uniqueid.SimpleUid;
import com.jfireframework.context.JfireContext;
import com.jfireframework.context.aop.AopUtil;
import com.jfireframework.context.bean.Bean;
import com.jfireframework.context.util.AnnotationUtil;
import com.jfireframework.mvc.annotation.RequestMapping;
import com.jfireframework.mvc.annotation.RequestParam;
import com.jfireframework.mvc.binder.DataBinder;
import com.jfireframework.mvc.binder.DataBinderFactory;
import com.jfireframework.mvc.binder.ParamInfo;
import com.jfireframework.mvc.binder.impl.HttpRequestBinder;
import com.jfireframework.mvc.binder.impl.HttpResponseBinder;
import com.jfireframework.mvc.binder.impl.HttpSessionBinder;
import com.jfireframework.mvc.binder.impl.ServletContextBinder;
import com.jfireframework.mvc.config.ResultType;
import com.jfireframework.mvc.core.Action;
import com.jfireframework.mvc.core.ModelAndView;
import com.jfireframework.mvc.interceptor.ActionInterceptor;
import com.jfireframework.mvc.rest.RestfulUrlTool;

public class ActionFactory
{
    
    private static final AescComparator AESC_COMPARATOR = new AescComparator();
    private static final SimpleUid      uid             = new SimpleUid();
    
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
        RequestMapping requestMapping = AnnotationUtil.getAnnotation(RequestMapping.class, method);
        actionInfo.setRequestMethod(requestMapping.method());
        actionInfo.setDataBinders(generateBinders(method));
        actionInfo.setReadStream(requestMapping.readStream());
        actionInfo.setEntity(bean.getInstance());
        setResultType(actionInfo);
        actionInfo.setContentType(requestMapping.contentType());
        actionInfo.setToken(requestMapping.token());
        if (actionInfo.getToken().equals(""))
        {
            actionInfo.setToken(uid.generate());
        }
        if (requestMapping.value().equals("/"))
        {
            ;
        }
        else
        {
            actionInfo.setRest(requestMapping.rest());
            if (requestMapping.value().indexOf("{") != -1 && requestMapping.value().indexOf("}") != -1)
            {
                actionInfo.setRest(true);
            }
            if (actionInfo.isRest())
            {
                if (StringUtil.isNotBlank(requestMapping.value()))
                {
                    requestPath += requestMapping.value();
                }
                else
                {
                    requestPath += "/" + method.getName();
                }
                if (requestPath.indexOf("{") == -1)
                {
                    for (DataBinder each : actionInfo.getDataBinders())
                    {
                        if (
                            each instanceof HttpSessionBinder //
                                    || each instanceof HttpRequestBinder //
                                    || each instanceof HttpResponseBinder //
                                    || each instanceof ServletContextBinder
                        )
                        {
                            continue;
                        }
                        requestPath += "/{" + each.getParamName() + "}";
                    }
                }
                actionInfo.setRestfulRule(RestfulUrlTool.build(requestPath));
            }
            else
            {
                requestPath += (StringUtil.isNotBlank(requestMapping.value()) ? requestMapping.value() : "/" + method.getName());
            }
        }
        actionInfo.setRequestUrl(requestPath);
        try
        {
            // 使用原始方法的名称和参数类型数组,在类型中获取真实的方法。这一步主要是防止action类本身被增强后，却仍然调用未增强的方法。
            Method realMethod = bean.getType().getMethod(method.getName(), method.getParameterTypes());
            actionInfo.setMethodAccessor(ReflectUtil.fastMethod(realMethod));
        }
        catch (Exception e)
        {
            throw new JustThrowException(e);
        }
        Bean[] beans = jfireContext.getBeanByInterface(ActionInterceptor.class);
        List<ActionInterceptor> interceptors = new ArrayList<>();
        next: for (Bean each : beans)
        {
            ActionInterceptor interceptor = (ActionInterceptor) each.getInstance();
            String rule = interceptor.pathRule();
            if ("*".equals(rule))
            {
                interceptors.add(interceptor);
                continue next;
            }
            else
            {
                for (String singleRule : rule.split(";"))
                {
                    if (isInterceptored(requestPath, singleRule))
                    {
                        interceptors.add(interceptor);
                        continue next;
                    }
                }
            }
            String token = interceptor.tokenRule();
            if (token != null && actionInfo.getToken().equals(token))
            {
                interceptors.add(interceptor);
            }
            
        }
        Collections.sort(interceptors, AESC_COMPARATOR);
        actionInfo.setInterceptors(interceptors.toArray(new ActionInterceptor[interceptors.size()]));
        return new Action(actionInfo);
    }
    
    private static boolean isInterceptored(String requestPath, String rule)
    {
        String[] rules = rule.split("\\*");
        int index = 0;
        for (int i = 0; i < rules.length; i++)
        {
            index = requestPath.indexOf(rules[i], index);
            if (index < 0)
            {
                return false;
            }
            index += rules[i].length();
        }
        return true;
    }
    
    private static void setResultType(ActionInfo info)
    {
        Method method = info.getMethod();
        if (method.getAnnotation(RequestMapping.class).resultType() == ResultType.AUTO)
        {
            Class<?> type = method.getReturnType();
            if (type == String.class)
            {
                info.setResultType(ResultType.Redirect);
            }
            else if (type == Void.class)
            {
                info.setResultType(ResultType.None);
            }
            else if (type == ModelAndView.class)
            {
                if (method.getParameterTypes().length == 0)
                {
                    info.setResultType(ResultType.Html);
                }
                else
                {
                    info.setResultType(ResultType.Beetl);
                }
            }
            else if (type == byte[].class)
            {
                info.setResultType(ResultType.Bytes);
            }
            else
            {
                info.setResultType(ResultType.Json);
            }
        }
        else
        {
            info.setResultType(method.getAnnotation(RequestMapping.class).resultType());
        }
    }
    
    private static DataBinder[] generateBinders(Method method)
    {
        if (method.getParameterTypes().length == 0)
        {
            return new DataBinder[0];
        }
        Class<?>[] paramTypes = method.getParameterTypes();
        String[] paramNames = getParamNames(method);
        Annotation[][] annotations = method.getParameterAnnotations();
        DataBinder[] dataBinders = new DataBinder[paramNames.length];
        for (int i = 0; i < paramNames.length; i++)
        {
            ParamInfo info = new ParamInfo();
            info.setAnnotations(annotations[i]);
            info.setEntityClass(paramTypes[i]);
            info.setPrefix(paramNames[i]);
            dataBinders[i] = DataBinderFactory.build(info, new HashSet<Class<?>>());
        }
        return dataBinders;
    }
    
    /**
     * 获取方法的参数名称数组，如果没有注解则使用形参名称，如果有，则该参数采用注解的名称
     * 
     * @param method
     * @return
     */
    private static String[] getParamNames(Method method)
    {
        String[] paramNames;
        try
        {
            paramNames = AopUtil.getParamNames(method);
        }
        catch (Exception e)
        {
            paramNames = new String[method.getParameterTypes().length];
        }
        Annotation[][] annos = AnnotationUtil.getParameterAnnotations(method);
        for (int i = 0; i < annos.length; i++)
        {
            if (annos[i].length == 0)
            {
                continue;
            }
            else
            {
                for (Annotation each : annos[i])
                {
                    if (each instanceof RequestParam)
                    {
                        paramNames[i] = ((RequestParam) each).value();
                        break;
                    }
                }
            }
        }
        return paramNames;
    }
}
