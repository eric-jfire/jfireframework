package com.jfireframework.mvc.binder.field.impl;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.mvc.annotation.MvcDateParse;

@SuppressWarnings("restriction")
public class CalendarField extends AbstractBinderField
{
    
    private ThreadLocal<SimpleDateFormat> format;
    private String                        style;
    private Field                         field;
    
    public CalendarField(String prefix, Field field, Set<Class<?>> cycleSet)
    {
        super(prefix, field, cycleSet);
        this.field = field;
        if (field.isAnnotationPresent(MvcDateParse.class))
        {
            style = field.getAnnotation(MvcDateParse.class).date_format();
        }
        else
        {
            style = "yyyy-MM-dd HH:mm:ss";
        }
        format = new ThreadLocal<SimpleDateFormat>() {
            @Override
            protected SimpleDateFormat initialValue()
            {
                return new SimpleDateFormat(style);
            }
        };
    }
    
    @Override
    protected void set(Object entity, String value)
    {
        try
        {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(format.get().parse(value));
            unsafe.putObject(entity, offset, calendar);
        }
        catch (ParseException e)
        {
            throw new UnSupportException(StringUtil.format("错误的日期转换格式，无法解析。实际值为{},转换格式为{}.请检查{}.{}", value, style, field.getDeclaringClass().getName(), field.getName()));
        }
    }
}
