package com.jfireframework.litl.format.impl;

import java.text.SimpleDateFormat;
import com.jfireframework.litl.format.Format;

public class DateFormat implements Format
{
    
    @Override
    public String format(Object data, String pattern)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(data);
    }
    
}
