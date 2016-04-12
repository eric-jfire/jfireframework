package com.jfireframework.fose.serializer.array;

import com.jfireframework.baseutil.collection.ObjectCollect;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;

public class LongArraySerializer extends AbstractArraySerializer
{
    @Override
    protected void writeOneDimensionMember(Object src, ByteBuf<?> buf, boolean first, ObjectCollect collect)
    {
        long[] array = (long[]) src;
        if (first == false)
        {
            buf.writeInt(array.length);
        }
        for (long l : array)
        {
            buf.writeMutableLengthLong(l);
        }
    }
    
    @Override
    @SuppressWarnings("rawtypes")
    protected Object readOneDimensionMember(Object target, Integer length, ByteBuf<?> buf, ObjectCollect collect, Class rootType)
    {
        long[] array = target == null ? new long[length] : (long[]) target;
        length = array.length;
        for (int i = 0; i < length; i++)
        {
            array[i] = buf.readMutableLengthLong();
        }
        return array;
    }
    
}
