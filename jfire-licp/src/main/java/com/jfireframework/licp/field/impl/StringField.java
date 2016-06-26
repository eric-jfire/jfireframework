package com.jfireframework.licp.field.impl;

import java.lang.reflect.Field;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;

public class StringField extends AbstractCacheField
{
    
    public StringField(Field field)
    {
        super(field);
    }
    
    @Override
    protected void writeSingle(Object holder, ByteBuf<?> buf, Licp licp)
    {
        String value = (String) unsafe.getObject(holder, offset);
        write(value, buf);
    }
    
    private void write(String value, ByteBuf<?> buf)
    {
        if (value == null)
        {
            buf.writeInt(Licp.NULL);
            return;
        }
        int length = value.length();
        buf.writeInt(length + 1);
        buf.writeString(value);
        for (int i = 0; i < length; i++)
        {
            buf.writeChar(value.charAt(i));
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
        String[] array = (String[]) oneDimArray;
        buf.writeInt(array.length + 1);
        for (String each : array)
        {
            write(each, buf);
        }
        
    }
    
    @Override
    protected void readSingle(Object holder, ByteBuf<?> buf, Licp licp)
    {
        unsafe.putObject(holder, offset, read(buf));
    }
    
    private String read(ByteBuf<?> buf)
    {
        int length = buf.readInt();
        if (length == 0)
        {
            return null;
        }
        length -= 1;
        char[] value = new char[length];
        for (int i = 0; i < length; i++)
        {
            value[i] = buf.readChar();
        }
        return new String(value);
    }
    
    @Override
    protected Object readOneDimArray(int length, ByteBuf<?> buf, Licp licp)
    {
        String[] array = new String[length];
        for (int i = 0; i < length; i++)
        {
            array[i] = read(buf);
        }
        return array;
    }
    
}
