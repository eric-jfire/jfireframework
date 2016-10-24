package com.jfireframework.licp.serializer.array;

import java.nio.ByteBuffer;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;
import com.jfireframework.licp.util.BufferUtil;

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
    
    @Override
    public Object deserialize(ByteBuffer buf, Licp licp)
    {
        int length = BufferUtil.readPositive(buf);
        short[] array = new short[length];
        licp.putObject(array);
        for (int i = 0; i < length; i++)
        {
            array[i] = BufferUtil.readShort(buf);
        }
        return array;
    }
    
}
