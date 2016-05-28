package com.jfireframework.codejson.function.impl.read;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.codejson.function.JsonReader;

public class DateReader implements JsonReader
{
    private static ThreadLocal<SimpleDateFormat> formats = new ThreadLocal<SimpleDateFormat>() {
        protected SimpleDateFormat initialValue()
        {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };
    
    @Override
    public Object read(Type entityType, Object value)
    {
        try
        {
            return formats.get().parse((String) value);
        }
        catch (ParseException e)
        {
            throw new JustThrowException(e);
        }
    }
    
}
