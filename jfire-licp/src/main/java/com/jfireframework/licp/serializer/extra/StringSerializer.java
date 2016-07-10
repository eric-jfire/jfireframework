package com.jfireframework.licp.serializer.extra;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;
import com.jfireframework.licp.serializer.LicpSerializer;

public class StringSerializer implements LicpSerializer
{
    
    @Override
    public void serialize(Object src, ByteBuf<?> buf, Licp licp)
    {
        String value = (String) src;
//        int length = value.length();
//        if (length == 0)
//        {
//            buf.writePositive(0);
//        }
//        else
//        {
//            buf.writePositive(length);
//            for (int i = 0; i < length; i++)
//            {
//                buf.writeChar(value.charAt(i));
//            }
//        }
        buf.writeString(value);
    }
    
    @Override
    public Object deserialize(ByteBuf<?> buf, Licp licp)
    {
        String value = buf.readString();
        licp.putObject(value);
        return value;
//        int length = buf.readPositive();
//        if (length == 0)
//        {
//            String result = "";
//            licp.putObject(result);
//            return result;
//        }
//        else
//        {
//            char[] src = new char[length];
//            for (int i = 0; i < length; i++)
//            {
//                src[i] = buf.readChar();
//            }
//            String result = new String(src);
//            licp.putObject(result);
//            return result;
//        }
    }
    
}
