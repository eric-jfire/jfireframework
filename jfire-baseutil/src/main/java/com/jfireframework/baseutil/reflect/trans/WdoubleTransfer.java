package com.jfireframework.baseutil.reflect.trans;

import com.jfireframework.baseutil.StringUtil;

public class WdoubleTransfer implements Transfer
{
    
    @Override
    public Object trans(String value)
    {
        if (StringUtil.isNotBlank(value))
        {
            return Double.valueOf(value);
        }
        else
        {
            return null;
        }
    }
    
}
