package com.jfireframework.baseutil.reflect.trans;

import com.jfireframework.baseutil.StringUtil;

public class WfloatTransfer implements Transfer
{
    
    @Override
    public Object trans(String value)
    {
        if (StringUtil.isNotBlank(value))
        {
            return Float.valueOf(value);
        }
        else
        {
            return null;
        }
    }
    
}
