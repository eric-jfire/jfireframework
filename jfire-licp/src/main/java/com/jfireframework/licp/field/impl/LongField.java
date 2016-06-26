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
    protected void writeSingle(Object holder, ByteBuf<?> buf, Licp licp)
    {
        buf.writeLong(unsafe.getLong(holder, offset));
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
            buf.writeLong(each);
        }
    }
    
    @Override
    protected void readSingle(Object holder, ByteBuf<?> buf, Licp licp)
    {
        unsafe.putLong(holder, offset, buf.readLong());
    }
    
    @Override
    protected Object readOneDimArray(int length, ByteBuf<?> buf, Licp licp)
    {
        long[] array = new long[length];
        for (int i = 0; i < array.length; i++)
        {
            array[i] = buf.readLong();
        }
        return array;
    }
    
}
