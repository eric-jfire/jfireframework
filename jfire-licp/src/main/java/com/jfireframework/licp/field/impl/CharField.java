package com.jfireframework.licp.field.impl;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;
import com.jfireframework.licp.util.BufferUtil;

public class CharField extends AbstractCacheField
{
    
    public CharField(Field field)
    {
        super(field);
    }
    
    @Override
    public void write(Object holder, ByteBuf<?> buf, Licp licp)
    {
        char c = unsafe.getChar(holder, offset);
        buf.writeVarChar(c);
    }
    
    @Override
    public void read(Object holder, ByteBuf<?> buf, Licp licp)
    {
        unsafe.putChar(holder, offset, buf.readVarChar());
    }
    
    @Override
    public void read(Object holder, ByteBuffer buf, Licp licp)
    {
        unsafe.putChar(holder, offset, BufferUtil.readVarChar(buf));
        
    }
    
}
