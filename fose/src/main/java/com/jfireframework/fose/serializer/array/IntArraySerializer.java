package com.jfireframework.fose.serializer.array;

import com.jfireframework.baseutil.collection.ObjectCollect;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;

public class IntArraySerializer extends AbstractArraySerializer
{
    @Override
    protected void writeOneDimensionMember(Object src, ByteBuf<?> buf, boolean first, ObjectCollect collect)
    {
        int[] array = (int[]) src;
        if (first == false)
        {
            buf.writeInt(array.length);
        }
        for (int i : array)
        {
            buf.writeInt(i);
        }
    }
    
    @Override
    @SuppressWarnings("rawtypes")
    protected Object readOneDimensionMember(Object target, Integer length, ByteBuf<?> buf, ObjectCollect collect, Class rootType)
    {
        int[] array = target == null ? new int[length] : (int[]) target;
        length = array.length;
        for (int i = 0; i < length; i++)
        {
            array[i] = buf.readInt();
        }
        return array;
    }
}
