package com.jfireframework.fose.serializer.array;

import com.jfireframework.baseutil.collection.ObjectCollect;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;

public class DoubleArraySerializer extends AbstractArraySerializer
{
    
    @Override
    protected void writeOneDimensionMember(Object src, ByteBuf<?> buf, boolean first, ObjectCollect collect)
    {
        double[] array = (double[]) src;
        if (first == false)
        {
            buf.writeInt(array.length);
        }
        for (double d : array)
        {
            buf.writeDouble(d);
        }
    }
    
    @Override
    @SuppressWarnings("rawtypes")
    protected Object readOneDimensionMember(Object target, Integer length, ByteBuf<?> buf, ObjectCollect collect, Class rootType)
    {
        double[] array = target == null ? new double[length] : (double[]) target;
        length = array.length;
        for (int i = 0; i < length; i++)
        {
            array[i] = buf.readDouble();
        }
        return array;
    }
    
}
