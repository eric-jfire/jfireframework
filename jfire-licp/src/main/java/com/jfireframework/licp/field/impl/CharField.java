package com.jfireframework.licp.field.impl;

import java.lang.reflect.Field;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;

public class CharField extends AbstractCacheField
{
    
    public CharField(Field field)
    {
        super(field);
    }
    
    @Override
    protected void writeSingle(Object holder, ByteBuf<?> buf, Licp licp)
    {
        buf.writeChar(unsafe.getChar(holder, offset));
    }
    
    @Override
    protected void writeOneDimensionMember(Object oneDimArray, ByteBuf<?> buf, Licp licp)
    {
        if (oneDimArray == null)
        {
            buf.writeInt(Licp.NULL);
            return;
        }
        char[] array = (char[]) oneDimArray;
        buf.writeInt(array.length + 1);
        for (char each : array)
        {
            buf.writeChar(each);
        }
    }
    
    @Override
    protected void readSingle(Object holder, ByteBuf<?> buf, Licp licp)
    {
        unsafe.putChar(holder, offset, buf.readChar());
    }
    
    @Override
    protected Object readOneDimArray(int length, ByteBuf<?> buf, Licp licp)
    {
        int[] array = new int[length];
        for (int i = 0; i < array.length; i++)
        {
            array[i] = buf.readChar();
        }
        return array;
    }
    
}
