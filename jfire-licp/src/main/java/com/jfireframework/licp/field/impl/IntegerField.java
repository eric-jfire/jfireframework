package com.jfireframework.licp.field.impl;

import java.lang.reflect.Field;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;

public class IntegerField extends AbstractCacheField
{
    public IntegerField(Field field)
    {
        super(field);
    }
    
    @Override
    public void write(Object holder, ByteBuf<?> buf, Licp licp)
    {
        Integer value = (Integer) unsafe.getObject(holder, offset);
        if (value == null)
        {
            buf.put((byte) 0);
        }
        else
        {
            buf.put((byte) 1);
            buf.writeInt(value);
        }
    }
    
    @Override
    public void read(Object holder, ByteBuf<?> buf, Licp licp)
    {
        boolean exist = buf.get() == 1 ? true : false;
        if (exist)
        {
            Integer value = buf.readInt();
            unsafe.putObject(holder, offset, value);
        }
        else
        {
            unsafe.putObject(holder, offset, null);
        }
    }
    
}
