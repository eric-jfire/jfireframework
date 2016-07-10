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
        buf.writePositive(array.length);
        for (long each : array)
        {
            buf.writeVarLong(each);
        }
    }
    
    @Override
    public Object deserialize(ByteBuf<?> buf, Licp licp)
    {
        int length = buf.readPositive();
        long[] array = new long[length];
        licp.putObject(array);
        for (int i = 0; i < length; i++)
        {
            array[i] = buf.readVarLong();
        }
        return array;
    }
    
}
