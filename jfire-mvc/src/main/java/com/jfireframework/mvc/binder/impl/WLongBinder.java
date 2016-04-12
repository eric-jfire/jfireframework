package com.jfireframework.mvc.binder.impl;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.mvc.binder.AbstractDataBinder;

public class WLongBinder extends AbstractDataBinder
{
    
    public WLongBinder(String paramName)
    {
        super(paramName);
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
