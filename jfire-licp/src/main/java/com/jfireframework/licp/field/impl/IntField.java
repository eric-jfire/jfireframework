package com.jfireframework.licp.field.impl;

import java.lang.reflect.Field;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;

public class IntField extends AbstractCacheField
{
    
    public IntField(Field field)
    {
        super(field);
    }
    
    @Override
    protected void writeSingle(Object holder, ByteBuf<?> buf, Licp licp)
    {
        buf.writeInt(unsafe.getInt(holder, offset));
    }
    
    @Override
    protected void writeOneDimensionMember(Object oneDimArray, ByteBuf<?> buf, Licp licp)
    {
        if (oneDimArray == null)
        {
            buf.writeInt(Licp.NULL);
            return;
        }
        int[] array = (int[]) oneDimArray;
        buf.writeInt(array.length + 1);
        for (int each : array)
        {
            buf.writeInt(each);
        }
    }
    
    @Override
    protected void readSingle(Object holder, ByteBuf<?> buf, Licp licp)
    {
        unsafe.putInt(holder, offset, buf.readInt());
    }
    
    @Override
    protected Object readOneDimArray(int length, ByteBuf<?> buf, Licp licp)
    {
        int[] array = new int[length];
        for (int i = 0; i < array.length; i++)
        {
            array[i] = buf.readInt();
        }
        return array;
    }
    
}
