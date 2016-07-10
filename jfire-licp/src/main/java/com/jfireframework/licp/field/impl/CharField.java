package com.jfireframework.licp.field.impl;

import java.lang.reflect.Field;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;

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
        buf.writeChar(c);
    }
    
    @Override
    public void read(Object holder, ByteBuf<?> buf, Licp licp)
    {
        unsafe.putChar(holder, offset, buf.readChar());
    }
    
}
