package com.jfireframework.licp.serializer.array;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;

public class ShortArraySerializer extends AbstractArraySerializer
{
    
    public ShortArraySerializer(Class<?> type)
    {
        super(type);
    }
    
    @Override
    public void serialize(Object src, ByteBuf<?> buf, Licp licp)
    {
        short[] array = (short[]) src;
        buf.writeShort(array.length);
        for (short each : array)
        {
            buf.writeShort(each);
        }
    }
    
    @Override
    public Object deserialize(ByteBuf<?> buf, Licp licp)
    {
        int length = buf.readInt();
        short[] array = new short[length];
        for (int i = 0; i < length; i++)
        {
            array[i] = buf.readShort();
        }
        return array;
    }
    
}
