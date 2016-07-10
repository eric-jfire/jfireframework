package com.jfireframework.licp.field.impl;

import java.lang.reflect.Field;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;

public class LongField extends AbstractCacheField
{
    
    public LongField(Field field)
    {
        super(field);
    }
    
    @Override
    public void write(Object holder, ByteBuf<?> buf, Licp licp)
    {
        long value = unsafe.getLong(holder, offset);
        buf.writeVarLong(value);
    }
    
    @Override
    public void read(Object holder, ByteBuf<?> buf, Licp licp)
    {
        unsafe.putLong(holder, offset, buf.readVarLong());
    }
    
}
