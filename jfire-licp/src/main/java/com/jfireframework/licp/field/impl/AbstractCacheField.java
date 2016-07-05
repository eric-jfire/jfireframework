package com.jfireframework.licp.field.impl;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.licp.Licp;
import com.jfireframework.licp.field.CacheField;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public abstract class AbstractCacheField implements CacheField
{
    protected static final Unsafe unsafe = ReflectUtil.getUnsafe();
    protected final long          offset;
    protected final boolean       array;
    protected final boolean       elementSameType;
    protected final int           dim;
    protected final Class<?>[]    arrayTypes;
    protected final Class<?>      arrayRootType;
    protected final boolean       finalField;
    
    public AbstractCacheField(Field field)
    {
        offset = unsafe.objectFieldOffset(field);
        if (field.getType().isArray())
        {
            array = true;
            Class<?> type = field.getType();
            List<Class<?>> arrayTypes = new LinkedList<Class<?>>();
            int dim = 0;
            while (type.isArray())
            {
                dim += 1;
                type = type.getComponentType();
                arrayTypes.add(type);
            }
            this.dim = dim;
            arrayRootType = type;
            if (Modifier.isFinal(type.getModifiers()))
            {
                elementSameType = true;
            }
            else
            {
                elementSameType = false;
            }
            this.arrayTypes = arrayTypes.toArray(new Class<?>[arrayTypes.size()]);
            // 如果是数组，那么本身的类型就是final的
            finalField = false;
        }
        else
        {
            arrayRootType = null;
            elementSameType = false;
            array = false;
            dim = 0;
            arrayTypes = null;
            if (Modifier.isFinal(field.getType().getModifiers()))
            {
                finalField = true;
            }
            else
            {
                finalField = false;
            }
        }
        
    }
    
    @Override
    public void write(Object holder, ByteBuf<?> buf, Licp licp)
    {
        if (array)
        {
            Object array = unsafe.getObject(holder, offset);
            writeArray(array, dim, buf, licp);
        }
        else
        {
            writeSingle(holder, buf, licp);
        }
    }
    
    protected abstract void writeSingle(Object holder, ByteBuf<?> buf, Licp licp);
    
    private void writeArray(Object arrayObject, int dimension, ByteBuf<?> buf, Licp licp)
    {
        if (arrayObject == null)
        {
            buf.writeInt(Licp.NULL);
            return;
        }
        if (elementSameType)
        {
            if (dimension == 1)
            {
                writeOneDimensionMember(arrayObject, buf, licp);
            }
            else
            {
                // 非一维数组都可以表示为一个object，同时低一维的数组且非一维数组也是一个object
                Object[] array = (Object[]) arrayObject;
                int length = array.length;
                buf.writeInt(length + 1);
                dimension -= 1;
                for (int i = 0; i < length; i++)
                {
                    writeArray(array[i], dimension, buf, licp);
                }
            }
        }
        else
        {
            licp.serialize(arrayObject, buf);
        }
    }
    
    protected abstract void writeOneDimensionMember(Object oneDimArray, ByteBuf<?> buf, Licp licp);
    
    @Override
    public void read(Object holder, ByteBuf<?> buf, Licp licp)
    {
        if (array)
        {
            unsafe.putObject(holder, offset, readArray(dim, buf, licp));
        }
        else
        {
            readSingle(holder, buf, licp);
        }
        
    }
    
    protected abstract void readSingle(Object holder, ByteBuf<?> buf, Licp licp);
    
    private Object readArray(int dimension, ByteBuf<?> buf, Licp licp)
    {
        if (elementSameType)
        {
            int length = buf.readInt();
            if (length == Licp.NULL)
            {
                return null;
            }
            length -= 1;
            // 如果维度是1，则按照个数读取内容，并且组装一维成数组返回
            if (dimension == 1)
            {
                return readOneDimArray(length, buf, licp);
            }
            else
            {
                // 如果维度不是1，则分别读取每一个多维数组，采用递归的方式进行
                Object[] arrayObject = (Object[]) Array.newInstance(arrayTypes[this.dim - dimension], length);
                for (int i = 0; i < length; i++)
                {
                    arrayObject[i] = readArray(dimension - 1, buf, licp);
                }
                return arrayObject;
            }
        }
        else
        {
            return licp.deserialize(buf);
        }
    }
    
    protected abstract Object readOneDimArray(int length, ByteBuf<?> buf, Licp licp);
}
