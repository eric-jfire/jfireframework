package com.jfireframework.mvc.binder.impl;

import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.mvc.binder.ParamInfo;

public class WLongBinder extends AbstractDataBinder
{
    
    public WLongBinder(ParamInfo info, Set<Class<?>> cycleSet)
    {
        super(info, cycleSet);
    }
    
    @Override
    public Object binder(HttpServletRequest request, Map<String, String> map, HttpServletResponse response)
    {
        String value = map.get(paramName);
        if (StringUtil.isNotBlank(value))
        {
            return Long.valueOf(value);
        }
        else
        {
            return null;
        }
    }
    
}
