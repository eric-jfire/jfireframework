package com.jfireframework.licp.field.impl;

import java.lang.reflect.Field;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;

public class ObjectField extends AbstractCacheField
{
    
    public ObjectField(Field field)
    {
        super(field);
    }
    
    @Override
    public void write(Object holder, ByteBuf<?> buf, Licp licp)
    {
        Object value = unsafe.getObject(holder, offset);
        licp._serialize(value, buf);
    }
    
    @Override
    public void read(Object holder, ByteBuf<?> buf, Licp licp)
    {
        Object value = licp._deserialize(buf);
        unsafe.putObject(holder, offset, value);
    }
    
}
