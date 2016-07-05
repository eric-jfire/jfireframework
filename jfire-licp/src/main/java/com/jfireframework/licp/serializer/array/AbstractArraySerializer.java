package com.jfireframework.licp.serializer.array;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;
import com.jfireframework.licp.serializer.LicpSerializer;
import com.jfireframework.licp.serializer.SerializerFactory;

public abstract class AbstractArraySerializer implements LicpSerializer
{
    protected static final Charset CHARSET = Charset.forName("utf8");
    protected final boolean        elementSameType;
    protected final LicpSerializer rootSerializer;
    protected final int            dim;
    protected final Class<?>[]     arrayTypes;
    
    public AbstractArraySerializer(Class<?> type)
    {
        int dim = 0;
        List<Class<?>> arrayTypes = new LinkedList<Class<?>>();
        while (type.isArray())
        {
            dim += 1;
            type = type.getComponentType();
            arrayTypes.add(type);
        }
        this.arrayTypes = arrayTypes.toArray(new Class<?>[arrayTypes.size()]);
        if (Modifier.isFinal(type.getModifiers()))
        {
            elementSameType = true;
            rootSerializer = SerializerFactory.get(type);
        }
        else
        {
            elementSameType = false;
            rootSerializer = null;
        }
        this.dim = dim;
    }
    
    public void serialize(Object src, ByteBuf<?> buf, Licp licp)
    {
        writeArray(src, dim, buf, licp);
    }
    
    private void writeArray(Object array, int dim, ByteBuf<?> buf, Licp licp)
    {
        if (array == null)
        {
            buf.writeInt(Licp.NULL);
            return;
        }
        if (elementSameType)
        {
            if (dim == 1)
            {
                writeOneDim(array, buf, licp);
            }
            buf.writeInt(((Object[]) array).length + 1);
            int nextDim = dim - 1;
            for (Object each : (Object[]) array)
            {
                writeArray(each, nextDim, buf, licp);
            }
        }
        else
        {
            buf.writeInt(((Object[]) array).length + 1);
            for (Object each : (Object[]) array)
            {
                licp.serialize(each, buf);
            }
        }
    }
    
    protected abstract void writeOneDim(Object array, ByteBuf<?> buf, Licp licp);
    
    /**
     * 反序列化二进制字节到对象中去
     * 
     * @param target
     * @param cache
     * @param collect
     * @param register TODO
     */
    public Object deserialize(ByteBuf<?> buf, Licp licp)
    {
        if (elementSameType)
        {
            return readArray(buf, licp, dim);
        }
        else
        {
            int length = buf.readInt();
            if (length == 0)
            {
                return null;
            }
            length -= 1;
            Object[] array = (Object[]) Array.newInstance(arrayTypes[0], length);
            for (int i = 0; i < length; i++)
            {
                array[i] = licp.deserialize(buf);
            }
            return array;
        }
    }
    
    private Object readArray(ByteBuf<?> buf, Licp licp, int dim)
    {
        int length = buf.readInt();
        if (length == 0)
        {
            return null;
        }
        length -= 1;
        if (dim == 1)
        {
            return readOneDim(length, buf, licp);
        }
        else
        {
            Object[] array = (Object[]) Array.newInstance(arrayTypes[this.dim - dim], length);
            dim -= 1;
            for (int i = 0; i < length; i++)
            {
                array[i] = readArray(buf, licp, dim);
            }
            return array;
        }
    }
    
    protected abstract Object readOneDim(int length, ByteBuf<?> buf, Licp licp);
}
