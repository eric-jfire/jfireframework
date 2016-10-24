package com.jfireframework.licp.field.impl;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;
import com.jfireframework.licp.util.BufferUtil;

public class DoubleField extends AbstractCacheField
{
    
    public DoubleField(Field field)
    {
        super(field);
    }
    
    @Override
    public void write(Object holder, ByteBuf<?> buf, Licp licp)
    {
        double value = unsafe.getDouble(holder, offset);
        buf.writeDouble(value);
    }
    
    @Override
    public void read(Object holder, ByteBuf<?> buf, Licp licp)
    {
        unsafe.putDouble(holder, offset, buf.readDouble());
    }
    
    @Override
    public void read(Object holder, ByteBuffer buf, Licp licp)
    {
        unsafe.putDouble(holder, offset, BufferUtil.readDouble(buf));
    }
    
}
