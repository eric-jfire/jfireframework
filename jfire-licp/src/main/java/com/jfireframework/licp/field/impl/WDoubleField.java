package com.jfireframework.licp.field.impl;

import java.lang.reflect.Field;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;

public class WDoubleField extends AbstractCacheField
{
    
    public WDoubleField(Field field)
    {
        super(field);
    }
    
    @Override
    protected void writeSingle(Object holder, ByteBuf<?> buf, Licp licp)
    {
        Double value = (Double) unsafe.getObject(holder, offset);
        write(buf, value);
    }
    
    private void write(ByteBuf<?> buf, Double value)
    {
        if (value == null)
        {
            buf.writeInt(Licp.NULL);
        }
        else
        {
            buf.writeInt(Licp.EXIST);
            buf.writeDouble(value.doubleValue());
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
        Double[] array = (Double[]) oneDimArray;
        buf.writeInt(array.length + 1);
        for (Double each : array)
        {
            write(buf, each);
        }
    }
    
    @Override
    protected void readSingle(Object holder, ByteBuf<?> buf, Licp licp)
    {
        unsafe.putObject(holder, offset, read(buf));
    }
    
    private Double read(ByteBuf<?> buf)
    {
        boolean exist = buf.readInt() == Licp.EXIST;
        if (exist)
        {
            return Double.valueOf(buf.readDouble());
        }
        else
        {
            return null;
        }
    }
    
    @Override
    protected Object readOneDimArray(int length, ByteBuf<?> buf, Licp licp)
    {
        Double[] array = new Double[length];
        for (int i = 0; i < array.length; i++)
        {
            array[i] = read(buf);
        }
        return array;
    }
    
}
