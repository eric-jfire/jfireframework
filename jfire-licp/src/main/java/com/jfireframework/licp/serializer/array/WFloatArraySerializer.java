package com.jfireframework.licp.serializer.array;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;

public class WFloatArraySerializer extends AbstractArraySerializer
{
    
    public WFloatArraySerializer()
    {
        super(Float[].class);
    }
    
    @Override
    public void serialize(Object src, ByteBuf<?> buf, Licp licp)
    {
        Float[] array = (Float[]) src;
        buf.writeInt(array.length);
        for (Float each : array)
        {
            if (each == null)
            {
                buf.put((byte) 0);
            }
            else
            {
                buf.put((byte) 1);
                buf.writeFloat(each);
            }
        }
    }
    
    @Override
    public Object deserialize(ByteBuf<?> buf, Licp licp)
    {
        int length = buf.readInt();
        Float[] array = new Float[length];
        for (int i = 0; i < length; i++)
        {
            boolean exist = buf.get() == 1 ? true : false;
            if (exist)
            {
                array[i] = buf.readFloat();
            }
            else
            {
                array[i] = null;
            }
        }
        return array;
    }
    
}
