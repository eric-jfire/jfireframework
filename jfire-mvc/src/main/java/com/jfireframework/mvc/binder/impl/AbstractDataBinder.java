package com.jfireframework.mvc.binder.impl;

import java.util.Set;
import com.jfireframework.mvc.binder.DataBinder;
import com.jfireframework.mvc.binder.ParamInfo;

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
