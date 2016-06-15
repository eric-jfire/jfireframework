package com.jfireframework.baseutil.reflect.trans;

import com.jfireframework.baseutil.StringUtil;

public class WlongTransfer implements Transfer
{
    
    @Override
    public Object trans(String value)
    {
        if (StringUtil.isNotBlank(value))
        {
            return Long.valueOf(value);
        }
        else
        {
            return null;
        }
    }
    
}
