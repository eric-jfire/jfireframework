package com.jfireframework.mvc.binder.field.impl;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.mvc.annotation.MvcDateParse;

@SuppressWarnings("restriction")
public class CalendarField extends AbstractBinderField
{
    
    private ThreadLocal<SimpleDateFormat> format;
    private String                        style;
    private Field                         field;
    
    public CalendarField(String prefix, Field field)
    {
        super(prefix, field);
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
            protected SimpleDateFormat initialValue()
            {
                return new SimpleDateFormat(style);
            }
        };
    }
    
    @Override
    public Object setValue(HttpServletRequest request, Object entity, Map<String, String> map, HttpServletResponse response) throws InstantiationException, IllegalAccessException
    {
        String value = map.get(name);
        if (StringUtil.isNotBlank(value))
        {
            if (entity == null)
            {
                entity = type.newInstance();
            }
            try
            {
                
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(format.get().parse(value));
                unsafe.putObject(entity, offset, calendar);
            }
            catch (ParseException e)
            {
                throw new RuntimeException(StringUtil.format("错误的日期转换格式，无法解析。实际值为{},转换格式为{}.请检查{}.{}", value, style, field.getDeclaringClass().getName(), field.getName()));
            }
        }
        return entity;
    }
}
