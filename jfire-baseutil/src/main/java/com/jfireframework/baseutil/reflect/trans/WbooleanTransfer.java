package com.jfireframework.baseutil.reflect.trans;

import com.jfireframework.baseutil.StringUtil;

public class WbooleanTransfer implements Transfer
{
    
    @Override
    public Object trans(String value)
    {
        if (StringUtil.isNotBlank(value))
        {
            return Boolean.valueOf(value);
        }
        else
        {
            return null;
        }
    }
    
}
