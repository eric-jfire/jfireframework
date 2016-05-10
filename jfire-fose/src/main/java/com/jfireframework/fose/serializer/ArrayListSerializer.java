package com.jfireframework.fose.serializer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import com.jfireframework.baseutil.collection.ObjectCollect;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.fose.BeanSerializerFactory;
import com.jfireframework.fose.ClassNoRegister;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class ArrayListSerializer implements Serializer
{
    private static long   dataOffset;
    private static long   sizeOffset;
    private static Unsafe unsafe = ReflectUtil.getUnsafe();
    static
    {
        try
        {
            Field datavalue = ArrayList.class.getDeclaredField("elementData");
            dataOffset = unsafe.objectFieldOffset(datavalue);
            Field sizeValue = ArrayList.class.getDeclaredField("size");
            sizeOffset = unsafe.objectFieldOffset(sizeValue);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public void getObjects(Object src, ObjectCollect collect)
    {
        if (collect.add(src))
        {
            for (Object each : (Collection) src)
            {
                BeanSerializerFactory.getSerializer(each.getClass()).getObjects(each, collect);
            }
        }
        
    }
    
    @Override
    public void serialize(Object src, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
    {
        Collection<?> collection = (Collection<?>) src;
        buf.writeInt(collection.size());
        for (Object each : collection)
        {
            buf.writeInt(collect.indexOf(each));
        }
        
    }
    
    @Override
    public void deserialize(Object target, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
    {
        int length = buf.readInt();
        Object[] array = new Object[length];
        unsafe.putObject(target, dataOffset, array);
        for (int i = 0; i < length; i++)
        {
            array[i] = collect.get(buf.readInt());
        }
        unsafe.putInt(target, sizeOffset, length);
    }
    
}
