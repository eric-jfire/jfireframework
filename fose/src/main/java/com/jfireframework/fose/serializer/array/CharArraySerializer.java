package com.jfireframework.fose.serializer.array;

import com.jfireframework.baseutil.collection.ObjectCollect;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;

public class CharArraySerializer extends AbstractArraySerializer
{
    @Override
    protected void writeOneDimensionMember(Object src, ByteBuf<?> buf, boolean first, ObjectCollect collect)
    {
        char[] array = (char[]) src;
        if (first == false)
        {
            buf.writeInt(array.length);
        }
        for (char c : array)
        {
            buf.writeChar(c);
        }
    }
    
    @Override
    @SuppressWarnings("rawtypes")
    protected Object readOneDimensionMember(Object target, Integer length, ByteBuf<?> buf, ObjectCollect collect, Class rootType)
    {
        char[] array = target == null ? new char[length] : (char[]) target;
        length = array.length;
        for (int i = 0; i < length; i++)
        {
            array[i] = buf.readChar();
        }
        return array;
    }
    
}
