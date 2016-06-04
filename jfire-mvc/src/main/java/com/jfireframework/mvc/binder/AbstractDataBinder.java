package com.jfireframework.mvc.binder;

public abstract class AbstractDataBinder implements DataBinder
{
    /**
     * 方法入参名称
     */
    protected String   paramName;
    /**
     * 方法入参类型
     */
    protected Class<?> entityClass;
    
    public AbstractDataBinder(String paramName, Class<?> entityClass)
    {
        this.paramName = paramName;
        this.entityClass = entityClass;
    }
    
    public AbstractDataBinder(String paramName)
    {
        this.paramName = paramName;
    }
    
    public String getParamName()
    {
        return paramName;
    }
}
