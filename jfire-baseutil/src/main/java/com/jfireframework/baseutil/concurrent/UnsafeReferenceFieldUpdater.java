package com.jfireframework.baseutil.concurrent;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.verify.Verify;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public final class UnsafeReferenceFieldUpdater<T, V>
{
    private final static Unsafe unsafe = ReflectUtil.getUnsafe();
    private final long          offset;
    
    public UnsafeReferenceFieldUpdater(Class<T> holderType, String fieldName)
    {
        try
        {
            Field field = holderType.getDeclaredField(fieldName);
            Verify.True(Modifier.isVolatile(field.getModifiers()), "属性必须是volatile修饰");
            offset = ReflectUtil.getFieldOffset(fieldName, holderType);
        }
        catch (NoSuchFieldException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public boolean compareAndSwap(T holder, V excepted, V newValue)
    {
        return unsafe.compareAndSwapObject(holder, offset, excepted, newValue);
    }
    
    public void orderSet(T holder, V newValue)
    {
        unsafe.putOrderedObject(holder, offset, newValue);
    }
    
    @SuppressWarnings("unchecked")
    public V getAndSet(T holder, V newValue)
    {
        while (true)
        {
            V current = (V) unsafe.getObjectVolatile(holder, offset);
            if (unsafe.compareAndSwapObject(holder, offset, current, newValue))
            {
                return current;
            }
        }
    }
}
