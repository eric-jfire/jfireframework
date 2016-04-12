package com.jfireframework.mvc.binder;

import com.jfireframework.mvc.annotation.RequestParam;

public abstract class AbstractDataBinder implements DataBinder
{
    /**
     * 方法入参名称
     */
    protected String       paramName;
    /**
     * 方法入参类型
     */
    protected Class<?>     entityClass;
    protected RequestParam requestParam;
    
    public AbstractDataBinder(String paramName, Class<?> entityClass)
    {
        this.paramName = paramName;
        this.entityClass = entityClass;
    }
    
    public AbstractDataBinder(String paramName)
    {
        this.paramName = paramName;
    }
    
    public AbstractDataBinder(RequestParam requestParam, String paramName)
    {
        this.paramName = paramName;
        this.requestParam = requestParam;
    }
    
    public String getParamName()
    {
        return paramName;
    }
}
