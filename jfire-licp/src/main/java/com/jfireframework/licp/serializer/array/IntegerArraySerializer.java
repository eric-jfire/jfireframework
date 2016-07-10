package com.jfireframework.licp.serializer.array;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;

public class IntegerArraySerializer extends AbstractArraySerializer
{
    
    public IntegerArraySerializer()
    {
        super(Integer[].class);
    }
    
    @Override
    public void serialize(Object src, ByteBuf<?> buf, Licp licp)
    {
        Integer[] array = (Integer[]) src;
        buf.writePositive(array.length);
        for (Integer each : array)
        {
            if (each == null)
            {
                buf.put((byte) 0);
            }
            else
            {
                buf.put((byte) 1);
                buf.writeVarint(each);
            }
        }
    }
    
    @Override
    public Object deserialize(ByteBuf<?> buf, Licp licp)
    {
        int length = buf.readPositive();
        Integer[] array = new Integer[length];
        licp.putObject(array);
        for (int i = 0; i < length; i++)
        {
            boolean exist = buf.get() == 1 ? true : false;
            if (exist)
            {
                array[i] = buf.readVarint();
            }
            else
            {
                array[i] = null;
            }
        }
        return array;
    }
    
}
