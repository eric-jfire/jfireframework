package com.jfireframework.licp.field.impl;

import java.lang.reflect.Field;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;

public class WlongField extends AbstractCacheField
{

    public WlongField(Field field)
    {
        super(field);
    }
    
    @Override
    protected void writeSingle(Object holder, ByteBuf<?> buf, Licp licp)
    {
        Long value = (Long) unsafe.getObject(holder, offset);
        write(buf, value);
    }
    
    private void write(ByteBuf<?> buf, Long value)
    {
        if (value == null)
        {
            buf.writeInt(Licp.NULL);
        }
        else
        {
            buf.writeInt(Licp.EXIST);
            buf.writeLong(value.longValue());
        }
    }
    
    @Override
    protected void writeOneDimensionMember(Object oneDimArray, ByteBuf<?> buf, Licp licp)
    {
        if (oneDimArray == null)
        {
            buf.writeInt(Licp.NULL);
            return;
        }
        Long[] array = (Long[]) oneDimArray;
        buf.writeInt(array.length + 1);
        for (Long each : array)
        {
            write(buf, each);
        }
    }
    
    @Override
    protected void readSingle(Object holder, ByteBuf<?> buf, Licp licp)
    {
        unsafe.putObject(holder, offset, read(buf));
    }
    
    private Long read(ByteBuf<?> buf)
    {
        boolean exist = buf.readInt() == Licp.EXIST;
        if (exist)
        {
            return Long.valueOf(buf.readLong());
        }
        else
        {
            return null;
        }
    }
    
    @Override
    protected Object readOneDimArray(int length, ByteBuf<?> buf, Licp licp)
    {
        Long[] array = new Long[length];
        for (int i = 0; i < array.length; i++)
        {
            array[i] = read(buf);
        }
        return array;
    }
    
}
