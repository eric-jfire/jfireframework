package com.jfireframework.licp.serializer.array;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;

public class LongArraySerializer extends AbstractArraySerializer
{
    
    public LongArraySerializer()
    {
        super(long[].class);
    }
    
    @Override
    public void serialize(Object src, ByteBuf<?> buf, Licp licp)
    {
        long[] array = (long[]) src;
        buf.writeInt(array.length);
        for (long each : array)
        {
            buf.writeLong(each);
        }
    }
    
    @Override
    public Object deserialize(ByteBuf<?> buf, Licp licp)
    {
        int length = buf.readInt();
        long[] array = new long[length];
        for (int i = 0; i < length; i++)
        {
            array[i] = buf.readLong();
        }
        return array;
    }
    
}
