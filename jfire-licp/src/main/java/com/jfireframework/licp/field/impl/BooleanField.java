package com.jfireframework.licp.field.impl;

import java.lang.reflect.Field;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;

public class BooleanField extends AbstractCacheField
{
    
    public BooleanField(Field field)
    {
        super(field);
    }
    
    @Override
    protected void writeSingle(Object holder, ByteBuf<?> buf, Licp licp)
    {
        buf.writeBoolean(unsafe.getBoolean(holder, offset));
    }
    
    @Override
    protected void writeOneDimensionMember(Object oneDimArray, ByteBuf<?> buf, Licp licp)
    {
        if (oneDimArray == null)
        {
            buf.writeInt(Licp.NULL);
            return;
        }
        boolean[] array = (boolean[]) oneDimArray;
        buf.writeInt(array.length + 1);
        for (boolean each : array)
        {
            buf.writeBoolean(each);
        }
    }
    
    @Override
    protected void readSingle(Object holder, ByteBuf<?> buf, Licp licp)
    {
        unsafe.putBoolean(holder, offset, buf.readBoolean());
    }
    
    @Override
    protected Object readOneDimArray(int length, ByteBuf<?> buf, Licp licp)
    {
        boolean[] array = new boolean[length];
        for (int i = 0; i < array.length; i++)
        {
            array[i] = buf.readBoolean();
        }
        return array;
    }
    
}
