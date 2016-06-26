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
    protected void writeSingle(Object holder, ByteBuf<?> buf, Licp licp)
    {
        Integer value = (Integer) unsafe.getObject(holder, offset);
        write(buf, value);
    }
    
    private void write(ByteBuf<?> buf, Integer value)
    {
        if (value == null)
        {
            buf.writeInt(Licp.NULL);
        }
        else
        {
            buf.writeInt(Licp.EXIST);
            buf.writeInt(value.intValue());
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
        Integer[] array = (Integer[]) oneDimArray;
        buf.writeInt(array.length + 1);
        for (Integer each : array)
        {
            write(buf, each);
        }
    }
    
    @Override
    protected void readSingle(Object holder, ByteBuf<?> buf, Licp licp)
    {
        unsafe.putObject(holder, offset, read(buf));
    }
    
    private Integer read(ByteBuf<?> buf)
    {
        boolean exist = buf.readInt() == Licp.EXIST;
        if (exist)
        {
            return Integer.valueOf(buf.readInt());
        }
        else
        {
            return null;
        }
    }
    
    @Override
    protected Object readOneDimArray(int length, ByteBuf<?> buf, Licp licp)
    {
        Integer[] array = new Integer[length];
        for (int i = 0; i < array.length; i++)
        {
            array[i] = read(buf);
        }
        return array;
    }
}
