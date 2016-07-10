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
    public void write(Object holder, ByteBuf<?> buf, Licp licp)
    {
        String value = (String) unsafe.getObject(holder, offset);
        if (value == null)
        {
            buf.writePositive(0);
        }
        else
        {
            int length = value.length();
            buf.writePositive((length << 1) | 1);
            for (int i = 0; i < length; i++)
            {
                buf.writeVarChar(value.charAt(i));
            }
        }
    }
    
    @Override
    public void read(Object holder, ByteBuf<?> buf, Licp licp)
    {
        int length = buf.readPositive();
        if (length == 0)
        {
            unsafe.putObject(holder, offset, null);
        }
        else
        {
            length >>>= 1;
            if (length == 0)
            {
                unsafe.putObject(holder, offset, "");
            }
            else
            {
                char[] src = new char[length];
                for (int i = 0; i < length; i++)
                {
                    src[i] = buf.readVarChar();
                }
                unsafe.putObject(holder, offset, new String(src));
            }
        }
    }
    
}
