package com.jfireframework.mvc.binder.field.impl;

import java.lang.reflect.Field;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.mvc.annotation.MvcParse;

@SuppressWarnings("restriction")
public class DateField extends AbstractBinderField
{
    private ThreadLocal<SimpleDateFormat> format;
    private boolean                       sqlData;
    private String                        style;
    private Field                         field;
    
    public DateField(String prefix, Field field)
    {
        super(prefix, field);
        this.field = field;
        if (field.getType() == Date.class)
        {
            sqlData = true;
        }
        else
        {
            sqlData = false;
        }
        if (field.isAnnotationPresent(MvcParse.class))
        {
            style = field.getAnnotation(MvcParse.class).date_format();
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
        return entity;
    }
}
