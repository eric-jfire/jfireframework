package com.jfireframework.fose.serializer.array;

import com.jfireframework.baseutil.collection.ObjectCollect;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;

public class FloatArraySerializer extends AbstractArraySerializer
{
    @Override
    protected void writeOneDimensionMember(Object src, ByteBuf<?> buf, boolean first, ObjectCollect collect)
    {
        float[] array = (float[]) src;
        if (first == false)
        {
            buf.writeInt(array.length);
        }
        for (float f : array)
        {
            buf.writeFloat(f);
        }
    }
    
    @Override
    @SuppressWarnings("rawtypes")
    protected Object readOneDimensionMember(Object target, Integer length, ByteBuf<?> buf, ObjectCollect collect, Class rootType)
    {
        float[] array = target == null ? new float[length] : (float[]) target;
        length = array.length;
        for (int i = 0; i < length; i++)
        {
            array[i] = buf.readFloat();
        }
        return array;
    }
    
}
