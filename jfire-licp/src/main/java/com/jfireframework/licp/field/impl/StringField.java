package com.jfireframework.licp.field.impl;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;

public class StringField extends AbstractCacheField
{
    private static final Charset CHARSET = Charset.forName("utf8");
    
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
            byte[] src = value.getBytes(CHARSET);
            buf.writeInt(((src.length << 1) | 1));
            buf.put(src);
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
                byte[] src = new byte[length];
                buf.get(src, length);
                unsafe.putObject(holder, offset, new String(src, CHARSET));
            }
        }
    }
    
}
