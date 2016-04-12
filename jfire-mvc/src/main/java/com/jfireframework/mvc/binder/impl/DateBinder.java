package com.jfireframework.mvc.binder.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.mvc.annotation.RequestParam;
import com.jfireframework.mvc.binder.AbstractDataBinder;

public class DateBinder extends AbstractDataBinder
{
    protected ThreadLocal<SimpleDateFormat> formats;
    
    public DateBinder(RequestParam requestParam, String paramName)
    {
        super(requestParam, paramName);
        String format = requestParam == null ? "" : requestParam.dateFormat();
        final String result = format.equals("") ? "yyyy-MM-dd HH:mm:ss" : format;
        formats = new ThreadLocal<SimpleDateFormat>() {
            protected SimpleDateFormat initialValue()
            {
                return new SimpleDateFormat(result);
            }
        };
    }
    
    @Override
    public Object binder(HttpServletRequest request, Map<String, String> map, HttpServletResponse response)
    {
        String value = map.get(paramName);
        if (StringUtil.isNotBlank(value))
        {
            try
            {
                return formats.get().parse(value);
            }
            catch (ParseException e)
            {
                throw new RuntimeException("日期转换出错", e);
            }
        }
        else
        {
            return null;
        }
    }
    
}
