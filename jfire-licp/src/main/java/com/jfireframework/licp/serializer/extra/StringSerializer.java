package com.jfireframework.licp.serializer.extra;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;
import com.jfireframework.licp.serializer.LicpSerializer;

public class StringSerializer implements LicpSerializer
{
    // private static final Charset charset = Charset.forName("utf8");
    
    @Override
    public void serialize(Object src, ByteBuf<?> buf, Licp licp)
    {
        // byte[] value = ((String) src).getBytes(charset);
        // if (value.length == 0)
        // {
        // buf.writeInt(0);
        // }
        // else
        // {
        // buf.writeInt(value.length);
        // buf.put(value);
        // }
        String value = (String) src;
        int length = value.length();
        if (length == 0)
        {
            buf.writeInt(0);
        }
        else
        {
            buf.writeInt(length);
            for (int i = 0; i < length; i++)
            {
                buf.writeChar(value.charAt(i));
            }
        }
    }
    
    @Override
    public Object deserialize(ByteBuf<?> buf, Licp licp)
    {
        int length = buf.readInt();
        if (length == 0)
        {
            String result = "";
            licp.putObject(result);
            return result;
        }
        else
        {
            // byte[] src = new byte[length];
            // buf.get(src, length);
            // return new String(src, charset);
            char[] src = new char[length];
            for (int i = 0; i < length; i++)
            {
                src[i] = buf.readChar();
            }
            String result = new String(src);
            licp.putObject(result);
            return result;
        }
    }
    
}
