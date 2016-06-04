package com.jfireframework.mvc.binder.impl;

import java.lang.annotation.Annotation;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.mvc.annotation.MvcDateParse;
import com.jfireframework.mvc.binder.AbstractDataBinder;
import com.jfireframework.mvc.binder.ParamInfo;

public class DateBinder extends AbstractDataBinder
{
    protected ThreadLocal<SimpleDateFormat> formats;
    
    public DateBinder(ParamInfo info, String paramName)
    {
        super(paramName);
        String format = "yyyy-MM-dd HH:mm:ss";
        for (Annotation each : info.getAnnotations())
        {
            if (each instanceof MvcDateParse)
            {
                format = ((MvcDateParse) each).date_format();
            }
        }
        final String result = format;
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
