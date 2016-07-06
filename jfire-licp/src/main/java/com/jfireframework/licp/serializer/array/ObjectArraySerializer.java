package com.jfireframework.licp.serializer.array;

import java.lang.reflect.Array;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;

public class ObjectArraySerializer extends AbstractArraySerializer
{
    
    public ObjectArraySerializer(Class<?> type)
    {
        super(type);
    }
    
    @Override
    public void serialize(Object src, ByteBuf<?> buf, Licp licp)
    {
        Object[] array = (Object[]) src;
        buf.writeInt(array.length);
        if (elementSameType)
        {
            for (Object each : array)
            {
                licp._serialize(each, buf, elementSerializer);
            }
        }
        else
        {
            for (Object each : array)
            {
                licp._serialize(each, buf);
            }
        }
        
    }
    
    @Override
    public Object deserialize(ByteBuf<?> buf, Licp licp)
    {
        int length = buf.readInt();
        Object[] array = (Object[]) Array.newInstance(elementType, length);
        for (int i = 0; i < length; i++)
        {
            if (elementSameType)
            {
                array[i] = licp._deserialize(buf, elementSerializer);
            }
            else
            {
                array[i] = licp._deserialize(buf);
            }
        }
        return array;
    }
    
}
