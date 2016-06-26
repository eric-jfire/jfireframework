package com.jfireframework.licp.field.impl;

import java.lang.reflect.Field;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;

public class FloatField extends AbstractCacheField
{
    
    public FloatField(Field field)
    {
        super(field);
    }
    
    @Override
    protected void writeSingle(Object holder, ByteBuf<?> buf, Licp licp)
    {
        buf.writeFloat(unsafe.getFloat(holder, offset));
    }
    
    @Override
    protected void writeOneDimensionMember(Object oneDimArray, ByteBuf<?> buf, Licp licp)
    {
        if (oneDimArray == null)
        {
            buf.writeInt(Licp.NULL);
            return;
        }
        float[] array = (float[]) oneDimArray;
        buf.writeInt(array.length + 1);
        for (float each : array)
        {
            buf.writeFloat(each);
        }
    }
    
    @Override
    protected void readSingle(Object holder, ByteBuf<?> buf, Licp licp)
    {
        unsafe.putFloat(holder, offset, buf.readFloat());
    }
    
    @Override
    protected Object readOneDimArray(int length, ByteBuf<?> buf, Licp licp)
    {
        float[] array = new float[length];
        for (int i = 0; i < array.length; i++)
        {
            array[i] = buf.readFloat();
        }
        return array;
    }
    
}
