package com.jfireframework.licp.field.impl;

import java.lang.reflect.Field;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;

public class ByteField extends AbstractCacheField
{
    
    public ByteField(Field field)
    {
        super(field);
    }
    
    @Override
    protected void writeSingle(Object holder, ByteBuf<?> buf, Licp licp)
    {
        buf.put(unsafe.getByte(holder, offset));
    }
    
    @Override
    protected void writeOneDimensionMember(Object oneDimArray, ByteBuf<?> buf, Licp licp)
    {
        if (oneDimArray == null)
        {
            buf.writeInt(Licp.NULL);
            return;
        }
        byte[] array = (byte[]) oneDimArray;
        buf.writeInt(array.length + 1);
        for (byte each : array)
        {
            buf.put(each);
        }
    }
    
    @Override
    protected void readSingle(Object holder, ByteBuf<?> buf, Licp licp)
    {
        unsafe.putByte(holder, offset, buf.get());
    }
    
    @Override
    protected Object readOneDimArray(int length, ByteBuf<?> buf, Licp licp)
    {
        byte[] array = new byte[length];
        for (Byte i = 0; i < array.length; i++)
        {
            array[i] = buf.get();
        }
        return array;
    }
    
}
