package com.jfireframework.licp.serializer.array;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;

public class DoubleArraySerializer extends AbstractArraySerializer
{
    
    public DoubleArraySerializer()
    {
        super(double[].class);
    }
    
    @Override
    public void serialize(Object src, ByteBuf<?> buf, Licp licp)
    {
        double[] array = (double[]) src;
        buf.writeInt(array.length);
        for (double each : array)
        {
            buf.writeDouble(each);
        }
    }
    
    @Override
    public Object deserialize(ByteBuf<?> buf, Licp licp)
    {
        int length = buf.readInt();
        double[] array = new double[length];
        for (int i = 0; i < length; i++)
        {
            array[i] = buf.readDouble();
        }
        return array;
    }
    
}
