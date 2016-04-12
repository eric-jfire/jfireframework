package com.jfireframework.fose.serializer.array;

import com.jfireframework.baseutil.collection.ObjectCollect;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;

public class BooleanArraySerializer extends AbstractArraySerializer
{
    
    @SuppressWarnings("rawtypes")
    protected Object readOneDimensionMember(Object target, Integer length, ByteBuf<?> buf, ObjectCollect collect, Class rootType)
    {
        boolean[] array = target == null ? new boolean[length] : (boolean[]) target;
        length = array.length;
        for (int i = 0; i < length; i++)
        {
            if (buf.get() > 0)
            {
                array[i] = true;
            }
            else
            {
                array[i] = false;
            }
        }
        return array;
    }
    
    @Override
    protected void writeOneDimensionMember(Object src, ByteBuf<?> buf, boolean first, ObjectCollect collect)
    {
        boolean[] array = (boolean[]) src;
        if (first == false)
        {
            buf.writeInt(array.length);
        }
        for (boolean each : array)
        {
            buf.writeBoolean(each);
        }
    }
    
}
