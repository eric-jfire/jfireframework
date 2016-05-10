package com.jfireframework.fose.serializer;

import java.lang.reflect.Field;
import com.jfireframework.baseutil.collection.ObjectCollect;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.fose.ClassNoRegister;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class StringSerializer implements Serializer
{
    private static long   valueOffset;
    private static Unsafe unsafe = ReflectUtil.getUnsafe();
    static
    {
        try
        {
            Field value = String.class.getDeclaredField("value");
            valueOffset = unsafe.objectFieldOffset(value);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public void getObjects(Object src, ObjectCollect collect)
    {
        collect.add(src);
    }
    
    @Override
    public void serialize(Object src, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
    {
        char[] value = (char[]) unsafe.getObject(src, valueOffset);
        buf.writeCharArray(value);
    }
    
    @Override
    public void deserialize(Object target, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
    {
        char[] value = buf.readCharArray();
        unsafe.putObject(target, valueOffset, value);
    }
    
}
