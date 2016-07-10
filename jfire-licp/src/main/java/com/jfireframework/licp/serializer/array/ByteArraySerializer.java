package com.jfireframework.licp.serializer.array;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;

public class ByteArraySerializer extends AbstractArraySerializer
{
    
    public ByteArraySerializer()
    {
        super(byte[].class);
    }
    
    @Override
    public void serialize(Object src, ByteBuf<?> buf, Licp licp)
    {
        byte[] array = (byte[]) src;
        buf.writePositive(array.length);
        for (byte each : array)
        {
            buf.put(each);
        }
    }
    
    @Override
    public Object deserialize(ByteBuf<?> buf, Licp licp)
    {
        int length = buf.readPositive();
        byte[] array = new byte[length];
        licp.putObject(array);
        for (int i = 0; i < length; i++)
        {
            array[i] = buf.get();
        }
        return array;
    }
    
}
