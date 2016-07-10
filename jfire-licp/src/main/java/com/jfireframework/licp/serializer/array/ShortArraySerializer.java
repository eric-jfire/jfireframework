package com.jfireframework.licp.serializer.array;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;

public class ShortArraySerializer extends AbstractArraySerializer
{
    
    public ShortArraySerializer()
    {
        super(short[].class);
    }
    
    @Override
    public void serialize(Object src, ByteBuf<?> buf, Licp licp)
    {
        short[] array = (short[]) src;
        buf.writePositive(array.length);
        for (short each : array)
        {
            buf.writeShort(each);
        }
    }
    
    @Override
    public Object deserialize(ByteBuf<?> buf, Licp licp)
    {
        int length = buf.readPositive();
        short[] array = new short[length];
        licp.putObject(array);
        for (int i = 0; i < length; i++)
        {
            array[i] = buf.readShort();
        }
        return array;
    }
    
}
