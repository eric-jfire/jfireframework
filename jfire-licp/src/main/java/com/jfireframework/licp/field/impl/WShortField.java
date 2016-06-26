package com.jfireframework.licp.field.impl;

import java.lang.reflect.Field;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;

public class WShortField extends AbstractCacheField
{
    
    public WShortField(Field field)
    {
        super(field);
    }
    
    @Override
    protected void writeSingle(Object holder, ByteBuf<?> buf, Licp licp)
    {
        Short value = (Short) unsafe.getObject(holder, offset);
        write(buf, value);
    }
    
    private void write(ByteBuf<?> buf, Short value)
    {
        if (value == null)
        {
            buf.writeInt(Licp.NULL);
        }
        else
        {
            buf.writeInt(Licp.EXIST);
            buf.writeShort(value.shortValue());
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
        Short[] array = (Short[]) oneDimArray;
        buf.writeInt(array.length + 1);
        for (Short each : array)
        {
            write(buf, each);
        }
    }
    
    @Override
    protected void readSingle(Object holder, ByteBuf<?> buf, Licp licp)
    {
        unsafe.putObject(holder, offset, read(buf));
    }
    
    private Short read(ByteBuf<?> buf)
    {
        boolean exist = buf.readInt() == Licp.EXIST;
        if (exist)
        {
            return Short.valueOf(buf.readShort());
        }
        else
        {
            return null;
        }
    }
    
    @Override
    protected Object readOneDimArray(int length, ByteBuf<?> buf, Licp licp)
    {
        Short[] array = new Short[length];
        for (int i = 0; i < array.length; i++)
        {
            array[i] = read(buf);
        }
        return array;
    }
}
