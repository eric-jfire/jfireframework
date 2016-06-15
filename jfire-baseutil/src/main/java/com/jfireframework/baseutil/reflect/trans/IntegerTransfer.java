package com.jfireframework.baseutil.reflect.trans;

import com.jfireframework.baseutil.StringUtil;

public class IntegerTransfer implements Transfer
{
    
    @Override
    public Object trans(String value)
    {
        if (StringUtil.isNotBlank(value))
        {
            return Integer.valueOf(value);
        }
        else
        {
            return null;
        }
    }
    
}
