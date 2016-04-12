package com.jfireframework.mvc.binder;

import java.lang.reflect.Type;
import com.jfireframework.mvc.annotation.RequestParam;

public class ParamInfo
{
    private String       prefix;
    private Type         entityClass;
    private Object       defaultValue;
    private RequestParam requestParam;
    
    public RequestParam getRequestParam()
    {
        return requestParam;
    }
    
    public void setRequestParam(RequestParam requestParam)
    {
        this.requestParam = requestParam;
    }
    
    public String getPrefix()
    {
        return prefix;
    }
    
    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
    }
    
    public Type getEntityClass()
    {
        return entityClass;
    }
    
    public void setEntityClass(Type entityClass)
    {
        this.entityClass = entityClass;
    }
    
    public Object getDefaultValue()
    {
        return defaultValue;
    }
    
    public void setDefaultValue(Object defaultValue)
    {
        this.defaultValue = defaultValue;
    }
    
}
