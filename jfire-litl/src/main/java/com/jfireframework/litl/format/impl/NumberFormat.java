package com.jfireframework.litl.format.impl;

import java.text.DecimalFormat;
import com.jfireframework.litl.format.Format;

public class NumberFormat implements Format
{
    
    @Override
    public String format(Object data, String pattern)
    {
        DecimalFormat format = new DecimalFormat(pattern);
        return format.format(format);
    }
    
}
