package com.jfireframework.licp.field.impl;

import java.lang.reflect.Field;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.licp.Licp;

public class WBooleanField extends AbstractCacheField
{
    
    public WBooleanField(Field field)
    {
        super(field);
    }
    
    @Override
    protected void writeSingle(Object holder, ByteBuf<?> buf, Licp licp)
    {
        Boolean value = (Boolean) unsafe.getObject(holder, offset);
        write(buf, value);
    }
    
    /**
     * 写入值。0代表null，1代表true，2代表false
     * 
     * @param buf
     * @param value
     */
    private void write(ByteBuf<?> buf, Boolean value)
    {
        if (value == null)
        {
            buf.writeInt(Licp.NULL);
        }
        else
        {
            if (value)
            {
                buf.put((byte) 1);
            }
            else
            {
                buf.put((byte) 2);
            }
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
        Boolean[] array = (Boolean[]) oneDimArray;
        buf.writeInt(array.length + 1);
        for (Boolean each : array)
        {
            write(buf, each);
        }
    }
    
    @Override
    protected void readSingle(Object holder, ByteBuf<?> buf, Licp licp)
    {
        unsafe.putObject(holder, offset, read(buf));
    }
    
    private Boolean read(ByteBuf<?> buf)
    {
        int result = buf.readInt();
        switch (result)
        {
            case 0:
                return null;
            case 1:
                return true;
            case 2:
                return false;
            default:
                throw new UnSupportException("");
        }
    }
    
    @Override
    protected Object readOneDimArray(int length, ByteBuf<?> buf, Licp licp)
    {
        Boolean[] array = new Boolean[length];
        for (int i = 0; i < array.length; i++)
        {
            array[i] = read(buf);
        }
        return array;
    }
    
}
