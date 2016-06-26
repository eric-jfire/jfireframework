package com.jfireframework.licp.field.impl;

import java.lang.reflect.Field;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;

public class DoubleField extends AbstractCacheField
{
    
    public DoubleField(Field field)
    {
        super(field);
    }
    
    @Override
    protected void writeSingle(Object holder, ByteBuf<?> buf, Licp licp)
    {
        buf.writeDouble(unsafe.getDouble(holder, offset));
    }
    
    @Override
    protected void writeOneDimensionMember(Object oneDimArray, ByteBuf<?> buf, Licp licp)
    {
        if (oneDimArray == null)
        {
            buf.writeInt(Licp.NULL);
            return;
        }
        double[] array = (double[]) oneDimArray;
        buf.writeInt(array.length + 1);
        for (double each : array)
        {
            buf.writeDouble(each);
        }
    }
    
    @Override
    protected void readSingle(Object holder, ByteBuf<?> buf, Licp licp)
    {
        unsafe.putDouble(holder, offset, buf.readDouble());
    }
    
    @Override
    protected Object readOneDimArray(int length, ByteBuf<?> buf, Licp licp)
    {
        double[] array = new double[length];
        for (int i = 0; i < array.length; i++)
        {
            array[i] = buf.readDouble();
        }
        return array;
    }
    
}
