package com.jfireframework.mvc.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.mvc.annotation.ContentType;
import com.jfireframework.mvc.annotation.RequestMethod;
import com.jfireframework.mvc.binder.DataBinder;
import com.jfireframework.mvc.config.ResultType;
import com.jfireframework.mvc.interceptor.ActionInterceptor;
import com.jfireframework.mvc.rest.RestfulRule;
import com.jfireframework.mvc.util.ActionInfo;
import sun.reflect.MethodAccessor;

/**
 * 传统action类，用来代表一个事先定义的url地址响应，该url地址中不包含*这样的通配符
 * 
 * @author 林斌（windfire@zailanghua.com）
 * 
 */
@SuppressWarnings("restriction")
public class Action
{
    /** 调用该action的对象实例 */
    private final Object              actionEntity;
    private final DataBinder[]        dataBinders;
    // 该action方法的快速反射调用工具
    private final MethodAccessor      methodAccessor;
    // 该action响应的url地址
    private final String              requestUrl;
    private final boolean             rest;
    private final RestfulRule         restfulRule;
    private final boolean             readStream;
    private final RequestMethod[]     requestMethods;
    private final Method              method;
    private final String              contentType;
    private final ResultType          resultType;
    private final ActionInterceptor[] interceptors;
    
    public Action(ActionInfo info)
    {
        actionEntity = info.getEntity();
        dataBinders = info.getDataBinders();
        methodAccessor = info.getMethodAccessor();
        requestUrl = info.getRequestUrl();
        rest = info.isRest();
        restfulRule = info.getRestfulRule();
        readStream = info.isReadStream();
        requestMethods = info.getRequestMethods();
        method = info.getMethod();
        resultType = info.getResultType();
        if ("".equals(info.getContentType()))
        {
            switch (resultType)
            {
                case Json:
                    contentType = ContentType.JSON;
                    break;
                case Beetl:
                    contentType = ContentType.HTML;
                    break;
                case String:
                    contentType = ContentType.JSON;
                    break;
                case Jsp:
                    contentType = ContentType.HTML;
                    break;
                case Html:
                    contentType = ContentType.HTML;
                    break;
                case Redirect:
                    contentType = ContentType.HTML;
                    break;
                case None:
                    contentType = ContentType.STREAM;
                    break;
                case Bytes:
                    contentType = ContentType.STREAM;
                    break;
                case FreeMakrer:
                    throw new UnSupportException("尚未支持freemarker，建议使用beetl");
                default:
                    throw new UnSupportException("方法没有指定返回类型");
            }
        }
        else
        {
            contentType = info.getContentType();
        }
        interceptors = info.getInterceptors();
    }
    
    public Object invoke(Object[] params)
    {
        try
        {
            return methodAccessor.invoke(actionEntity, params);
        }
        catch (IllegalArgumentException | InvocationTargetException e)
        {
            throw new JustThrowException(e);
        }
    }
    
    public Object getActionEntity()
    {
        return actionEntity;
    }
    
    public DataBinder[] getDataBinders()
    {
        return dataBinders;
    }
    
    public MethodAccessor getMethodAccessor()
    {
        return methodAccessor;
    }
    
    public String getRequestUrl()
    {
        return requestUrl;
    }
    
    public boolean isRest()
    {
        return rest;
    }
    
    public RestfulRule getRestfulRule()
    {
        return restfulRule;
    }
    
    public boolean isReadStream()
    {
        return readStream;
    }
    
    public RequestMethod[] getRequestMethods()
    {
        return requestMethods;
    }
    
    public Method getMethod()
    {
        return method;
    }
    
    public String getContentType()
    {
        return contentType;
    }
    
    public ResultType getResultType()
    {
        return resultType;
    }
    
    public ActionInterceptor[] getInterceptors()
    {
        return interceptors;
    }
    
}
