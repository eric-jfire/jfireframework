package com.jfireframework.mvc.util;

import java.lang.reflect.Method;
import com.jfireframework.mvc.annotation.RequestMethod;
import com.jfireframework.mvc.binder.DataBinder;
import com.jfireframework.mvc.config.ResultType;
import com.jfireframework.mvc.interceptor.ActionInterceptor;
import com.jfireframework.mvc.rest.RestfulRule;
import sun.reflect.MethodAccessor;

@SuppressWarnings("restriction")
public class ActionInfo
{
    private Object              entity;
    private Method              method;
    private RequestMethod[]     requestMethods;
    private boolean             rest       = false;
    private DataBinder[]        dataBinders;
    protected String            requestUrl;
    private RestfulRule         restfulRule;
    private boolean             readStream = false;
    private String              contentType;
    private ResultType          resultType;
    // 该action方法的快速反射调用工具
    private MethodAccessor      methodAccessor;
    private ActionInterceptor[] interceptors;
    
    public ActionInterceptor[] getInterceptors()
    {
        return interceptors;
    }
    
    public void setInterceptors(ActionInterceptor[] interceptors)
    {
        this.interceptors = interceptors;
    }
    
    public MethodAccessor getMethodAccessor()
    {
        return methodAccessor;
    }
    
    public void setMethodAccessor(MethodAccessor methodAccessor)
    {
        this.methodAccessor = methodAccessor;
    }
    
    public ResultType getResultType()
    {
        return resultType;
    }
    
    public void setResultType(ResultType resultType)
    {
        this.resultType = resultType;
    }
    
    public Object getEntity()
    {
        return entity;
    }
    
    public void setEntity(Object entity)
    {
        this.entity = entity;
    }
    
    public DataBinder[] getDataBinders()
    {
        return dataBinders;
    }
    
    public void setDataBinders(DataBinder[] dataBinders)
    {
        this.dataBinders = dataBinders;
    }
    
    public String getRequestUrl()
    {
        return requestUrl;
    }
    
    public void setRequestUrl(String requestUrl)
    {
        this.requestUrl = requestUrl;
    }
    
    public RestfulRule getRestfulRule()
    {
        return restfulRule;
    }
    
    public void setRestfulRule(RestfulRule restfulRule)
    {
        this.restfulRule = restfulRule;
    }
    
    public boolean isReadStream()
    {
        return readStream;
    }
    
    public void setReadStream(boolean readStream)
    {
        this.readStream = readStream;
    }
    
    public String getContentType()
    {
        return contentType;
    }
    
    public void setContentType(String contentType)
    {
        this.contentType = contentType;
    }
    
    public boolean isRest()
    {
        return rest;
    }
    
    public void setRest(boolean rest)
    {
        this.rest = rest;
    }
    
    public Method getMethod()
    {
        return method;
    }
    
    public void setMethod(Method method)
    {
        this.method = method;
    }
    
    public RequestMethod[] getRequestMethods()
    {
        return requestMethods;
    }
    
    public void setRequestMethods(RequestMethod[] requestMethods)
    {
        this.requestMethods = requestMethods;
    }
    
}