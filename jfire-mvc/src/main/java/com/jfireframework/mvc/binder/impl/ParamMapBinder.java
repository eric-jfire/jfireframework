package com.jfireframework.mvc.binder.impl;

import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.binder.AbstractDataBinder;
import com.jfireframework.mvc.binder.ParamInfo;

public class ParamMapBinder extends AbstractDataBinder
{
    
    public ParamMapBinder(ParamInfo info, Set<Class<?>> cycleSet)
    {
        super(info, cycleSet);
    }
    
    @Override
    public Object binder(HttpServletRequest request, Map<String, String> map, HttpServletResponse response)
    {
        return map;
    }
    
}
