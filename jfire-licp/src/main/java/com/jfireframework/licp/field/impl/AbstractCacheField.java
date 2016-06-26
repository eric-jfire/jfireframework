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
    
    public AbstractCacheField(Field field)
    {
        offset = unsafe.objectFieldOffset(field);
        if (field.getType().isArray())
        {
            array = true;
            Class<?> type = field.getType();
            List<Class<?>> arrayTypes = new LinkedList<>();
            int dim = 0;
            while (type.isArray())
            {
                dim += 1;
                type = type.getComponentType();
                arrayTypes.add(type);
            }
            this.dim = dim;
            if (Modifier.isFinal(type.getModifiers()))
            {
                elementSameType = true;
            }
            else
            {
                elementSameType = false;
            }
            this.arrayTypes = arrayTypes.toArray(new Class<?>[arrayTypes.size()]);
        }
        else
        {
            elementSameType = false;
            array = false;
            dim = 0;
            arrayTypes = null;
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
        // 如果已经是一维数组，则将数组内容输出
        if (dimension == 1)
        {
            writeOneDimensionMember(arrayObject, buf, licp);
        }
        // 如果不是一维数组，则将数组的每一个内容继续该循环
        else
        {
            dimension--;
            // 非一维数组都可以表示为一个object，同时低一维的数组且非一维数组也是一个object
            Object[] array = (Object[]) arrayObject;
            int length = array.length;
            buf.writeInt(length + 1);
            for (int i = 0; i < length; i++)
            {
                writeArray(array[i], dimension, buf, licp);
            }
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
    
    protected abstract Object readOneDimArray(int length, ByteBuf<?> buf, Licp licp);
}
