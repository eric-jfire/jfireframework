package com.jfireframework.licp.field.impl;

import java.lang.reflect.Field;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;

public class BooleanField extends AbstractCacheField
{
    
    public BooleanField(Field field)
    {
        super(field);
    }
    
    @Override
    public void write(Object holder, ByteBuf<?> buf, Licp licp)
    {
        boolean value = unsafe.getBoolean(holder, offset);
        if (value)
        {
            buf.put((byte) 1);
        }
        else
        {
            buf.put((byte) 0);
        }
    }
    
    @Override
    public void read(Object holder, ByteBuf<?> buf, Licp licp)
    {
        boolean value = buf.get() == 1 ? true : false;
        unsafe.putBoolean(holder, offset, value);
    }
    
}
