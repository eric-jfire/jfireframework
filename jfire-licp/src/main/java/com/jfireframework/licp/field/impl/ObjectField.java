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
            objectSerializer.serialize(value, buf, licp);
        }
        else
        {
            SerializerFactory.get(value.getClass()).serialize(value, buf, licp);
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
            return objectSerializer.deserialize(buf, licp);
        }
        else
        {
            return desc(buf, licp);
        }
    }
    
    private Object desc(ByteBuf<?> buf, Licp licp)
    {
        int classNo = buf.readInt();
        if (classNo == 0)
        {
            return null;
        }
        classNo -= 1;
        Class<?> type;
        if (classNo == 0)
        {
            int length = buf.readInt();
            byte[] nameBytes = new byte[length];
            buf.get(nameBytes, length);
            type = licp.loadClass(new String(nameBytes));
        }
        else
        {
            type = licp.loadClass(classNo);
        }
        return SerializerFactory.get(type).deserialize(buf, licp);
    }
    
    @Override
    protected void writeOneDimensionMember(Object oneDimArray, ByteBuf<?> buf, Licp licp)
    {
        if (oneDimArray == null)
        {
            buf.writeInt(Licp.NULL);
            return;
        }
        Object[] array = (Object[]) oneDimArray;
        buf.writeInt(array.length + 1);
        for (Object each : array)
        {
            if (elementSameType)
            {
                rootSerializer.serialize(each, buf, licp);
            }
            else
            {
                SerializerFactory.get(each.getClass()).serialize(each, buf, licp);
            }
        }
        
    }
    
    @Override
    protected Object readOneDimArray(int length, ByteBuf<?> buf, Licp licp)
    {
        Class<?> type = arrayTypes[arrayTypes.length - 1];
        Object[] array = (Object[]) Array.newInstance(type, length);
        if (elementSameType)
        {
            for (int i = 0; i < length; i++)
            {
                array[i] = rootSerializer.deserialize(buf, licp);
            }
        }
        else
        {
            for (int i = 0; i < length; i++)
            {
                array[i] = desc(buf, licp);
            }
        }
        return array;
    }
}
