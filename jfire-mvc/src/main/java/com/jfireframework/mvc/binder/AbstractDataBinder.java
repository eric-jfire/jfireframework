package com.jfireframework.mvc.binder;

import java.util.Set;

public abstract class AbstractDataBinder implements DataBinder
{
    /**
     * 方法入参名称
     */
    protected final String paramName;
    
    public AbstractDataBinder(ParamInfo info, Set<Class<?>> cycleSet)
    {
        paramName = info.getPrefix();
    }
    
    @Override
    public String getParamName()
    {
        return paramName;
    }
}
