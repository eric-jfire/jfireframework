package com.jfireframework.licp.field.impl;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;

public class WByteField extends AbstractCacheField
{
    
    public WByteField(Field field)
    {
        super(field);
    }
    
    @Override
    public void write(Object holder, ByteBuf<?> buf, Licp licp)
    {
        Byte b = (Byte) unsafe.getObject(holder, offset);
        if (b == null)
        {
            buf.put((byte) 0);
        }
        else
        {
            buf.put((byte) 1);
            buf.put(b);
        }
    }
    
    @Override
    public void read(Object holder, ByteBuf<?> buf, Licp licp)
    {
        byte b = buf.get();
        if (b == 0)
        {
            unsafe.putObject(holder, offset, null);
        }
        else
        {
            b = buf.get();
            unsafe.putObject(holder, offset, b);
        }
    }
    
    @Override
    public void read(Object holder, ByteBuffer buf, Licp licp)
    {
        byte b = buf.get();
        if (b == 0)
        {
            unsafe.putObject(holder, offset, null);
        }
        else
        {
            b = buf.get();
            unsafe.putObject(holder, offset, b);
        }
    }
    
}
