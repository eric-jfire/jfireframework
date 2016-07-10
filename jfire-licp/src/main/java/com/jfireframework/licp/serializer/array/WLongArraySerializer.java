package com.jfireframework.licp.serializer.array;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;

public class WLongArraySerializer extends AbstractArraySerializer
{
    
    public WLongArraySerializer()
    {
        super(Long[].class);
    }
    
    @Override
    public void serialize(Object src, ByteBuf<?> buf, Licp licp)
    {
        Long[] array = (Long[]) src;
        buf.writePositive(array.length);
        for (Long each : array)
        {
            if (each == null)
            {
                buf.put((byte) 0);
            }
            else
            {
                buf.put((byte) 1);
                buf.writeLong(each);
            }
        }
    }
    
    @Override
    public Object deserialize(ByteBuf<?> buf, Licp licp)
    {
        int length = buf.readPositive();
        Long[] array = new Long[length];
        licp.putObject(array);
        for (int i = 0; i < length; i++)
        {
            boolean exist = buf.get() == 1 ? true : false;
            if (exist)
            {
                array[i] = buf.readLong();
            }
            else
            {
                array[i] = null;
            }
        }
        return array;
    }
}
