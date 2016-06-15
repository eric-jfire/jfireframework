package com.jfireframework.baseutil.reflect.trans;

public class BooleanTransfer implements Transfer
{
    
    @Override
    public Object trans(String value)
    {
        return Boolean.valueOf(value);
    }
    
}
