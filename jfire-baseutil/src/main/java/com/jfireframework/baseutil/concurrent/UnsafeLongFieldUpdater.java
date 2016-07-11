package com.jfireframework.baseutil.concurrent;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.verify.Verify;
import sun.misc.Unsafe;

public class UnsafeLongFieldUpdater<T>
{
    private final static Unsafe unsafe = ReflectUtil.getUnsafe();
    private final long          offset;
    
    public UnsafeLongFieldUpdater(Class<T> holderType, String fieldName)
    {
        try
        {
            Field field = holderType.getDeclaredField(fieldName);
            Verify.True(Modifier.isVolatile(field.getModifiers()), "属性必须是volatile修饰");
            Verify.True(field.getType() == long.class, "属性必须是int类型");
            offset = ReflectUtil.getFieldOffset(fieldName, holderType);
        }
        catch (NoSuchFieldException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public boolean compareAndSwap(T holder, long excepted, long newValue)
    {
        return unsafe.compareAndSwapLong(holder, offset, excepted, newValue);
    }
    
    public long getAndIncrement(T holder)
    {
        return getAndAdd(holder, 1);
    }
    
    public long getAndAdd(T holder, long add)
    {
        do
        {
            long oldValue = unsafe.getLongVolatile(holder, offset);
            long newValue = oldValue + add;
            if (unsafe.compareAndSwapLong(holder, offset, oldValue, newValue))
            {
                return oldValue;
            }
        } while (true);
    }
}
