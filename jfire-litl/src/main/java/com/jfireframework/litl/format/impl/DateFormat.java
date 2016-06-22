package com.jfireframework.litl.format.impl;

import java.text.SimpleDateFormat;
import com.jfireframework.litl.format.Format;

public class DateFormat implements Format
{
    private ThreadLocal<SimpleDateFormat> formats;
    
    public DateFormat(final String pattern)
    {
        formats = new ThreadLocal<SimpleDateFormat>() {
            protected SimpleDateFormat initialValue()
            {
                return new SimpleDateFormat(pattern);
            }
        };
    }
    
    @Override
    public String format(Object data)
    {
        return formats.get().format(data);
    }
    
}
