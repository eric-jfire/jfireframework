package com.jfireframework.licp.field.impl;

import java.lang.reflect.Field;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;

public class ShortField extends AbstractCacheField
{
    
    public ShortField(Field field)
    {
        super(field);
    }
    
    @Override
    protected void writeSingle(Object holder, ByteBuf<?> buf, Licp licp)
    {
        buf.writeShort(unsafe.getShort(holder, offset));
    }
    
    @Override
    protected void writeOneDimensionMember(Object oneDimArray, ByteBuf<?> buf, Licp licp)
    {
        if (oneDimArray == null)
        {
            buf.writeInt(Licp.NULL);
            return;
        }
        short[] array = (short[]) oneDimArray;
        buf.writeInt(array.length + 1);
        for (Short each : array)
        {
            buf.writeShort(each);
        }
    }
    
    @Override
    protected void readSingle(Object holder, ByteBuf<?> buf, Licp licp)
    {
        unsafe.putShort(holder, offset, buf.readShort());
    }
    
    @Override
    protected Object readOneDimArray(int length, ByteBuf<?> buf, Licp licp)
    {
        short[] array = new short[length];
        for (Short i = 0; i < array.length; i++)
        {
            array[i] = buf.readShort();
        }
        return array;
    }
    
}
