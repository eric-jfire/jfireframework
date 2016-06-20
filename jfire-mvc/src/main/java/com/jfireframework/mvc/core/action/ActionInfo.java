package com.jfireframework.mvc.core.action;

import java.lang.reflect.Method;
import com.jfireframework.mvc.binder.DataBinder;
import com.jfireframework.mvc.config.RequestMethod;
import com.jfireframework.mvc.config.ResultType;
import com.jfireframework.mvc.interceptor.ActionInterceptor;
import com.jfireframework.mvc.rule.HeaderRule;
import com.jfireframework.mvc.rule.RestfulRule;
import com.jfireframework.mvc.viewrender.ViewRender;
import sun.reflect.MethodAccessor;

@SuppressWarnings("restriction")
public class ActionInfo
{
    private Object              entity;
    private Method              method;
    private RequestMethod       requestMethod;
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
    private String              token;
    private ViewRender          viewRender;
    private HeaderRule          headerRule;
    private boolean             hasCookie;
    private boolean             hasHeaders;
    
    public boolean isHasCookie()
    {
        return hasCookie;
    }
    
    public void setHasCookie(boolean hasCookie)
    {
        this.hasCookie = hasCookie;
    }
    
    public boolean isHasHeaders()
    {
        return hasHeaders;
    }
    
    public void setHasHeaders(boolean hasHeaders)
    {
        this.hasHeaders = hasHeaders;
    }
    
    public void setHeaders(String[] headers)
    {
        headerRule = new HeaderRule(headers);
    }
    
    public HeaderRule getHeaderRule()
    {
        return headerRule;
    }
    
    public ViewRender getViewRender()
    {
        return viewRender;
    }
    
    public void setViewRender(ViewRender viewRender)
    {
        this.viewRender = viewRender;
    }
    
    public String getToken()
    {
        return token;
    }
    
    public void setToken(String token)
    {
        this.token = token;
    }
    
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
    
    public RequestMethod getRequestMethod()
    {
        return requestMethod;
    }
    
    public void setRequestMethod(RequestMethod requestMethod)
    {
        this.requestMethod = requestMethod;
    }
    
}
