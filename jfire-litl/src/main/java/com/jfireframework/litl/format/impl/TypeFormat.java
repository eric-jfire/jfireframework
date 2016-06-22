package com.jfireframework.litl.format.impl;

import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.litl.format.Format;
import com.jfireframework.litl.format.TypeFormatRegister;

public class TypeFormat implements Format
{
    private String          pattern;
    private volatile Format format;
    private final Object    lock = new Object();
    
    @Override
    public String format(Object data)
    {
        if (format == null)
        {
            synchronized (lock)
            {
                if (format == null)
                {
                    try
                    {
                        format = TypeFormatRegister.get(data.getClass()).newInstance();
                    }
                    catch (Exception e)
                    {
                        throw new JustThrowException(e);
                    }
                    format.setPattern(pattern);
                }
            }
        }
        return format.format(data);
    }
    
    @Override
    public void setPattern(String pattern)
    {
        this.pattern = pattern;
    }
    
}
