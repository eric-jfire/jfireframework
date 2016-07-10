package com.jfireframework.licp.serializer.array;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;

public class StringArraySerializer extends AbstractArraySerializer
{
    
    public StringArraySerializer()
    {
        super(String[].class);
    }
    
    @Override
    public void serialize(Object src, ByteBuf<?> buf, Licp licp)
    {
        String[] array = (String[]) src;
        buf.writePositive(array.length);
        for (String each : array)
        {
            if (each == null)
            {
                buf.writePositive(0);
            }
            else
            {
                int length = each.length();
                buf.writePositive((length << 1 | 1));
                for (int i = 0; i < length; i++)
                {
                    buf.writeVarChar(each.charAt(i));
                }
            }
        }
    }
    
    @Override
    public Object deserialize(ByteBuf<?> buf, Licp licp)
    {
        int length = buf.readPositive();
        String[] array = new String[length];
        licp.putObject(array);
        for (int i = 0; i < length; i++)
        {
            int strLength = buf.readPositive();
            if (strLength == 0)
            {
                array[i] = null;
            }
            else
            {
                strLength >>>= 1;
                if (strLength == 0)
                {
                    array[i] = "";
                }
                else
                {
                    char[] src = new char[strLength];
                    for (int j = 0; j < strLength; j++)
                    {
                        src[j] = buf.readVarChar();
                    }
                    array[i] = new String(src);
                }
            }
        }
        return array;
    }
    
}
