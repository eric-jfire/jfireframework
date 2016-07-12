package com.jfireframework.licp.serializer.array;

import java.lang.reflect.Array;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;
import com.jfireframework.licp.serializer.LicpSerializer;

public class ObjectArraySerializer extends AbstractArraySerializer
{
    private final LicpSerializer elementSerializer;
    
    public ObjectArraySerializer(Class<?> type, Licp licp)
    {
        super(type);
        if (elementSameType)
        {
            elementSerializer = licp._getSerializer(type.getComponentType());
        }
        else
        {
            elementSerializer = null;
        }
    }
    
    @Override
    public void serialize(Object src, ByteBuf<?> buf, Licp licp)
    {
        Object[] array = (Object[]) src;
        buf.writePositive(array.length);
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
        int length = buf.readPositive();
        Object[] array = (Object[]) Array.newInstance(elementType, length);
        licp.putObject(array);
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
