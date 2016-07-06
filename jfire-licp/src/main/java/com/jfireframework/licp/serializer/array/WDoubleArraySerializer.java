package com.jfireframework.licp.serializer.array;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;

public class WDoubleArraySerializer extends AbstractArraySerializer
{
    
    public WDoubleArraySerializer()
    {
        super(Double[].class);
    }
    
    @Override
    public void serialize(Object src, ByteBuf<?> buf, Licp licp)
    {
        Double[] array = (Double[]) src;
        buf.writeInt(array.length);
        for (Double each : array)
        {
            if (each == null)
            {
                buf.put((byte) 0);
            }
            else
            {
                buf.put((byte) 1);
                buf.writeDouble(each);
            }
        }
    }
    
    @Override
    public Object deserialize(ByteBuf<?> buf, Licp licp)
    {
        int length = buf.readInt();
        Double[] array = new Double[length];
        licp.putObject(array);
        for (int i = 0; i < length; i++)
        {
            boolean exist = buf.get() == 1 ? true : false;
            if (exist)
            {
                array[i] = buf.readDouble();
            }
            else
            {
                array[i] = null;
            }
        }
        return array;
    }
}
