package com.jfireframework.licp.field.impl;

import java.lang.reflect.Field;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;

public class IntField extends AbstractCacheField
{
    
    public IntField(Field field)
    {
        super(field);
    }
    
    @Override
    public void write(Object holder, ByteBuf<?> buf, Licp licp)
    {
        int value = unsafe.getInt(holder, offset);
        buf.writeVarint(value);
    }
    
    @Override
    public void read(Object holder, ByteBuf<?> buf, Licp licp)
    {
        unsafe.putInt(holder, offset, buf.readVarint());
    }
    
}
