package com.jfireframework.fose.serializer.array;

import com.jfireframework.baseutil.collection.ObjectCollect;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;

public class ByteArraySerializer extends AbstractArraySerializer
{
    
    @Override
    protected void writeOneDimensionMember(Object src, ByteBuf<?> buf, boolean first, ObjectCollect collect)
    {
        byte[] array = (byte[]) src;
        if (first == false)
        {
            buf.writeInt(array.length);
        }
        buf.put(array);
    }
    
    @Override
    @SuppressWarnings("rawtypes")
    protected Object readOneDimensionMember(Object target, Integer length, ByteBuf<?> buf, ObjectCollect collect, Class rootType)
    {
        byte[] array = target == null ? new byte[length] : (byte[]) target;
        buf.get(array, array.length);
        return array;
    }
    
}
