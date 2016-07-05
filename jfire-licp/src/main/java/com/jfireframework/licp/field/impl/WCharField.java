package com.jfireframework.licp.field.impl;

import java.lang.reflect.Field;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;

public class WCharField extends AbstractCacheField
{
    
    public WCharField(Field field)
    {
        super(field);
    }
    
    @Override
    protected void writeSingle(Object holder, ByteBuf<?> buf, Licp licp)
    {
        Character value = (Character) unsafe.getObject(holder, offset);
        write(buf, value);
    }
    
    private void write(ByteBuf<?> buf, Character value)
    {
        if (value == null)
        {
            buf.writeInt(Licp.NULL);
        }
        else
        {
            buf.writeInt(Licp.EXIST);
            buf.writeChar(value.charValue());
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
        Character[] array = (Character[]) oneDimArray;
        buf.writeInt(array.length + 1);
        for (Character each : array)
        {
            write(buf, each);
        }
    }
    
    @Override
    protected void readSingle(Object holder, ByteBuf<?> buf, Licp licp)
    {
        unsafe.putObject(holder, offset, read(buf));
    }
    
    private Character read(ByteBuf<?> buf)
    {
        boolean exist = buf.readInt() == Licp.EXIST;
        if (exist)
        {
            return Character.valueOf(buf.readChar());
        }
        else
        {
            return null;
        }
    }
    
    @Override
    protected Object readOneDimArray(int length, ByteBuf<?> buf, Licp licp)
    {
        Character[] array = new Character[length];
        for (int i = 0; i < array.length; i++)
        {
            array[i] = read(buf);
        }
        return array;
    }
}
