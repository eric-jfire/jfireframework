package com.jfireframework.mvc.binder.field.impl;

import java.lang.reflect.Field;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Set;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.mvc.annotation.MvcDateParse;

@SuppressWarnings("restriction")
public class DateField extends AbstractBinderField
{
    private ThreadLocal<SimpleDateFormat> format;
    private boolean                       sqlData;
    private String                        style;
    private Field                         field;
    
    public DateField(String prefix, Field field, Set<Class<?>> cycleSet)
    {
        super(prefix, field, cycleSet);
        this.field = field;
        if (field.getType() == Date.class)
        {
            sqlData = true;
        }
        else
        {
            sqlData = false;
        }
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
            if (sqlData)
            {
                unsafe.putObject(entity, offset, new Date(format.get().parse(value).getTime()));
            }
            else
            {
                unsafe.putObject(entity, offset, format.get().parse(value));
            }
        }
        catch (ParseException e)
        {
            throw new RuntimeException(StringUtil.format("错误的日期转换格式，无法解析。实际值为{},转换格式为{}.请检查{}.{}", value, style, field.getDeclaringClass().getName(), field.getName()));
        }
    }
}
