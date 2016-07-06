package com.jfireframework.licp.serializer.array;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;

public class WCharArraySerializer extends AbstractArraySerializer
{
    
    public WCharArraySerializer()
    {
        super(Character[].class);
    }
    
    @Override
    public void serialize(Object src, ByteBuf<?> buf, Licp licp)
    {
        Character[] array = (Character[]) src;
        buf.writeInt(array.length);
        for (Character each : array)
        {
            if (each == null)
            {
                buf.put((byte) 0);
            }
            else
            {
                buf.put((byte) 1);
                buf.writeChar(each);
            }
        }
    }
    
    @Override
    public Object deserialize(ByteBuf<?> buf, Licp licp)
    {
        int length = buf.readInt();
        Character[] array = new Character[length];
        for (int i = 0; i < length; i++)
        {
            boolean exist = buf.get() == 1 ? true : false;
            if (exist)
            {
                array[i] = buf.readChar();
            }
            else
            {
                array[i] = null;
            }
        }
        return array;
    }
}
