package com.jfireframework.mvc.binder.impl;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.annotation.CookieValue;
import com.jfireframework.mvc.binder.AbstractDataBinder;
import com.jfireframework.mvc.binder.ParamInfo;

public class CookieBinder extends AbstractDataBinder
{
    private final String cookieName;
    private final String defaultValue;
    
    public CookieBinder(ParamInfo info, Set<Class<?>> cycleSet)
    {
        super(info, cycleSet);
        String cookieName = "";
        String defaultValue = "";
        for (Annotation each : info.getAnnotations())
        {
            if (each instanceof CookieValue)
            {
                cookieName = ((CookieValue) each).value();
                defaultValue = ((CookieValue) each).defaultValue();
                break;
            }
        }
        if (cookieName.equals(""))
        {
            cookieName = paramName;
        }
        if (defaultValue.equals(""))
        {
            defaultValue = null;
        }
        this.cookieName = cookieName;
        this.defaultValue = defaultValue;
    }
    
    @Override
    public Object binder(HttpServletRequest request, Map<String, String> map, HttpServletResponse response)
    {
        Cookie[] cookies = request.getCookies();
        Cookie target = null;
        for (Cookie each : cookies)
        {
            if (each.getName().equals(cookieName))
            {
                target = each;
                break;
            }
        }
        if (target != null)
        {
            return target.getValue();
        }
        else
        {
            return defaultValue;
        }
    }
    
}
