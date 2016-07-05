package com.jfireframework.licp.field.impl;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;
import com.jfireframework.licp.serializer.LicpSerializer;
import com.jfireframework.licp.serializer.SerializerFactory;

public class ObjectField extends AbstractCacheField
{
    private final LicpSerializer objectSerializer;
    private final LicpSerializer rootSerializer;
    
    public ObjectField(Field field)
    {
        super(field);
        if (field.getType().isArray())
        {
            objectSerializer = null;
            if (elementSameType)
            {
                rootSerializer = SerializerFactory.get(arrayRootType);
            }
            else
            {
                rootSerializer = null;
            }
        }
        else
        {
            rootSerializer = null;
            if (finalField)
            {
                objectSerializer = SerializerFactory.get(field.getType());
            }
            else
            {
                objectSerializer = null;
            }
        }
    }
    
    @Override
    protected void writeSingle(Object holder, ByteBuf<?> buf, Licp licp)
    {
        Object value = unsafe.getObject(holder, offset);
        if (finalField)
        {
            if (value == null)
            {
                buf.writeInt(Licp.NULL);
                return;
            }
            else
            {
                buf.writeInt(Licp.EXIST);
            }
            objectSerializer.serialize(value, buf, licp);
        }
        else
        {
            licp.serialize(value, buf);
        }
    }
    
    @Override
    protected void readSingle(Object holder, ByteBuf<?> buf, Licp licp)
    {
        unsafe.putObject(holder, offset, read(buf, licp));
    }
    
    private Object read(ByteBuf<?> buf, Licp licp)
    {
        if (finalField)
        {
            int Null = buf.readInt();
            if (Null == Licp.NULL)
            {
                return null;
            }
            return objectSerializer.deserialize(buf, licp);
        }
        else
        {
            return licp.deserialize(buf);
        }
    }
    
    @Override
    protected void writeOneDimensionMember(Object oneDimArray, ByteBuf<?> buf, Licp licp)
    {
        Object[] array = (Object[]) oneDimArray;
        buf.writeInt(array.length + 1);
        for (Object each : array)
        {
            rootSerializer.serialize(each, buf, licp);
        }
        
    }
    
    @Override
    protected Object readOneDimArray(int length, ByteBuf<?> buf, Licp licp)
    {
        Class<?> type = arrayTypes[arrayTypes.length - 1];
        Object[] array = (Object[]) Array.newInstance(type, length);
        for (int i = 0; i < length; i++)
        {
            array[i] = rootSerializer.deserialize(buf, licp);
        }
        return array;
    }
}
