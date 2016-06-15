package com.jfireframework.baseutil.reflect.trans;

public class LongTransfer implements Transfer
{
    
    @Override
    public Object trans(String value)
    {
        return Long.valueOf(value);
    }
    
}
