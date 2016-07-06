package com.jfireframework.licp.serializer.extra;

import java.nio.charset.Charset;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;
import com.jfireframework.licp.serializer.LicpSerializer;

public class StringSerializer implements LicpSerializer
{
    private static final Charset charset = Charset.forName("utf8");
    
    @Override
    public void serialize(Object src, ByteBuf<?> buf, Licp licp)
    {
        byte[] value = ((String) src).getBytes(charset);
        if (value.length == 0)
        {
            buf.writeInt(0);
        }
        else
        {
            buf.writeInt(value.length);
            buf.put(value);
        }
    }
    
    @Override
    public Object deserialize(ByteBuf<?> buf, Licp licp)
    {
        int length = buf.readInt();
        if (length == 0)
        {
            return "";
        }
        else
        {
            byte[] src = new byte[length];
            buf.get(src, length);
            return new String(src, charset);
        }
    }
    
}
