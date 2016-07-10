package com.jfireframework.licp.field.impl;

import java.lang.reflect.Field;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;

public class ByteField extends AbstractCacheField
{
    
    public ByteField(Field field)
    {
        super(field);
    }
    
    @Override
    public void write(Object holder, ByteBuf<?> buf, Licp licp)
    {
        byte value = unsafe.getByte(holder, offset);
        buf.put(value);
    }
    
    @Override
    public void read(Object holder, ByteBuf<?> buf, Licp licp)
    {
        unsafe.putByte(holder, offset, buf.get());
    }
    
}
