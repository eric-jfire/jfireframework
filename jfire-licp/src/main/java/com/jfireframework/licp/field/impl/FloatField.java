package com.jfireframework.licp.field.impl;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;
import com.jfireframework.licp.util.BufferUtil;

public class FloatField extends AbstractCacheField
{
    
    public FloatField(Field field)
    {
        super(field);
    }
    
    @Override
    public void write(Object holder, ByteBuf<?> buf, Licp licp)
    {
        float value = unsafe.getFloat(holder, offset);
        buf.writeFloat(value);
    }
    
    @Override
    public void read(Object holder, ByteBuf<?> buf, Licp licp)
    {
        unsafe.putFloat(holder, offset, buf.readFloat());
    }
    
    @Override
    public void read(Object holder, ByteBuffer buf, Licp licp)
    {
        unsafe.putFloat(holder, offset, BufferUtil.readFloat(buf));
    }
    
}
