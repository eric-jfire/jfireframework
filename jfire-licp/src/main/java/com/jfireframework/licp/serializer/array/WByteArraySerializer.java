package com.jfireframework.licp.serializer.array;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;

public class WByteArraySerializer extends AbstractArraySerializer
{
    
    public WByteArraySerializer()
    {
        super(Byte[].class);
    }
    
    @Override
    public void serialize(Object src, ByteBuf<?> buf, Licp licp)
    {
        Byte[] array = (Byte[]) src;
        buf.writeInt(array.length);
        for (Byte each : array)
        {
            if (each == null)
            {
                buf.put((byte) 0);
            }
            else
            {
                buf.put((byte) 1);
                buf.put(each);
            }
        }
    }
    
    @Override
    public Object deserialize(ByteBuf<?> buf, Licp licp)
    {
        int length = buf.readInt();
        Byte[] array = new Byte[length];
        for (int i = 0; i < length; i++)
        {
            boolean exist = buf.get() == 1 ? true : false;
            if (exist)
            {
                array[i] = buf.get();
            }
            else
            {
                array[i] = null;
            }
        }
        return array;
    }
}
