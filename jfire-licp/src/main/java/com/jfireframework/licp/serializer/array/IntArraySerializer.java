package com.jfireframework.licp.serializer.array;

import java.nio.ByteBuffer;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;
import com.jfireframework.licp.util.BufferUtil;

public class IntArraySerializer extends AbstractArraySerializer
{
    
    public IntArraySerializer()
    {
        super(int[].class);
    }
    
    @Override
    public void serialize(Object src, ByteBuf<?> buf, Licp licp)
    {
        int[] array = (int[]) src;
        buf.writePositive(array.length);
        for (int each : array)
        {
            buf.writeVarint(each);
        }
    }
    
    @Override
    public Object deserialize(ByteBuf<?> buf, Licp licp)
    {
        int length = buf.readPositive();
        int[] array = new int[length];
        licp.putObject(array);
        for (int i = 0; i < length; i++)
        {
            array[i] = buf.readVarint();
        }
        return array;
    }
    
    @Override
    public Object deserialize(ByteBuffer buf, Licp licp)
    {
        int length = BufferUtil.readPositive(buf);
        int[] array = new int[length];
        licp.putObject(array);
        for (int i = 0; i < length; i++)
        {
            array[i] = BufferUtil.readVarint(buf);
        }
        return array;
    }
    
}
