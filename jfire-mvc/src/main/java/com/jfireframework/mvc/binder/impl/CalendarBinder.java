package com.jfireframework.mvc.binder.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.annotation.RequestParam;

public class CalendarBinder extends DateBinder
{
    
    public CalendarBinder(RequestParam requestParam, String paramName)
    {
        super(requestParam, paramName);
    }
    
    @Override
    public Object binder(HttpServletRequest request, Map<String, String> map, HttpServletResponse response)
    {
        Date date = (Date) super.binder(request, map, response);
        if (date != null)
        {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        }
        else
        {
            return null;
        }
    }
}
