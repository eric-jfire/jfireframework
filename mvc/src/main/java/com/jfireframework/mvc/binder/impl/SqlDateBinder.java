package com.jfireframework.mvc.binder.impl;

import java.util.Date;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.annotation.RequestParam;

public class SqlDateBinder extends DateBinder
{
    
    public SqlDateBinder(RequestParam requestParam, String paramName)
    {
        super(requestParam, paramName);
    }
    
    @Override
    public Object binder(HttpServletRequest request, Map<String, String> map, HttpServletResponse response)
    {
        Date date = (Date) super.binder(request, map, response);
        if (date != null)
        {
            return new java.sql.Date(date.getTime());
        }
        else
        {
            return null;
        }
    }
    
}
