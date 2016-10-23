package com.jfireframework.baseutil.concurrent;

import com.jfireframework.baseutil.reflect.ReflectUtil;
import sun.misc.Unsafe;

class CpuCachePadingLeft
{
    protected long p1, p2, p3, p4, p5, p6, p7;
}

class CpuCacheValue<T> extends CpuCachePadingLeft
{
    protected int        up;
    // 前后都有7个元素填充，可以保证该核心变量独自在一个缓存行中
    protected volatile T value;
    protected int        down;
}

public class CpuCachePadingRefence<T> extends CpuCacheValue<T>
{
    
    protected long              p9, p10, p11, p12, p13, p14, p15;
    private static final Unsafe unsafe        = ReflectUtil.getUnsafe();
    private static final long   refenceOffset = ReflectUtil.getFieldOffset("value", CpuCacheValue.class);
    
    public CpuCachePadingRefence(T value)
    {
        this.value = value;
    }
    
    public long shouleNotUse()
    {
        return p1 + p2 + p3 + p4 + p5 + p6 + p7 + up + Long.valueOf((String) value) + down + p9 + p10 + p11 + p12 + p13 + p14 + p15;
    }
    
    public void set(T newValue)
    {
        value = newValue;
    }
    
    public void orderSet(T newValue)
    {
        unsafe.putOrderedObject(this, refenceOffset, newValue);
    }
    
    public T get()
    {
        return value;
    }
    
    public boolean compareAndSwap(T expectedValue, T newValue)
    {
        return unsafe.compareAndSwapObject(this, refenceOffset, expectedValue, newValue);
    }
    
    // @SuppressWarnings("unchecked")
    // public T getAndSet(T newValue)
    // {
    // return (T) updater.getAndSet(this, newValue);
    // }
    //
    // public T setAndReturnOrigin(T newValue)
    // {
    // T t = refence;
    // if (unsafe.compareAndSwapObject(this, refenceOffset, t, newValue))
    // {
    // return t;
    // }
    // for (;; t = refence)
    // {
    // if (unsafe.compareAndSwapObject(this, refenceOffset, t, newValue))
    // {
    // return t;
    // }
    // }
    // }
}
