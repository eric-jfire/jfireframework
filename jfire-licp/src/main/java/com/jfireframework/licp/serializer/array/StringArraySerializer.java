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
        buf.writeInt(array.length);
        for (String each : array)
        {
            if (each == null)
            {
                buf.writeInt(0);
            }
            else
            {
                byte[] bytes = each.getBytes(CHARSET);
                buf.writeInt(((bytes.length << 1) | 1));
                buf.put(bytes);
            }
        }
    }
    
    @Override
    public Object deserialize(ByteBuf<?> buf, Licp licp)
    {
        int length = buf.readInt();
        String[] array = new String[length];
        licp.putObject(array);
        for (int i = 0; i < length; i++)
        {
            int strLength = buf.readInt();
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
                    byte[] src = new byte[strLength];
                    buf.get(src, strLength);
                    array[i] = new String(src, CHARSET);
                }
            }
        }
        return array;
    }
    
}
