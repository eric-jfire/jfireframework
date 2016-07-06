package com.jfireframework.licp.serializer.array;

import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import com.jfireframework.licp.serializer.LicpSerializer;
import com.jfireframework.licp.serializer.SerializerFactory;

public abstract class AbstractArraySerializer implements LicpSerializer
{
    protected static final Charset CHARSET = Charset.forName("utf8");
    protected final boolean        elementSameType;
    protected final LicpSerializer elementSerializer;
    protected final Class<?>       elementType;
    
    public AbstractArraySerializer(Class<?> type)
    {
        elementType = type.getComponentType();
        if (Modifier.isFinal(elementType.getModifiers()))
        {
            elementSameType = true;
            elementSerializer = SerializerFactory.get(type.getComponentType());
        }
        else
        {
            elementSameType = false;
            elementSerializer = null;
        }
    }
    
}
