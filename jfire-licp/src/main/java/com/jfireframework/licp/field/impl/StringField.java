package com.jfireframework.licp.field.impl;

import java.lang.reflect.Field;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;

public class StringField extends AbstractCacheField
{
    // private static final Charset CHARSET = Charset.forName("utf8");
    
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
            buf.writeInt(0);
        }
        else
        {
            // byte[] src = value.getBytes(CHARSET);
            // buf.writeInt(((src.length << 1) | 1));
            // buf.put(src);
            int length = value.length();
            buf.writeInt((length << 1) | 1);
            for (int i = 0; i < length; i++)
            {
                buf.writeChar(value.charAt(i));
            }
        }
    }
    
    @Override
    public void read(Object holder, ByteBuf<?> buf, Licp licp)
    {
        int length = buf.readInt();
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
                // byte[] src = new byte[length];
                // buf.get(src, length);
                // unsafe.putObject(holder, offset, new String(src, CHARSET));
                char[] src = new char[length];
                for (int i = 0; i < length; i++)
                {
                    src[i] = buf.readChar();
                }
                unsafe.putObject(holder, offset, new String(src));
            }
        }
    }
    
}
