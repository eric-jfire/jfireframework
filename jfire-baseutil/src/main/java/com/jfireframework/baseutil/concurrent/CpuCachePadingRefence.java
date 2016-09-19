package com.jfireframework.baseutil.concurrent;

import com.jfireframework.baseutil.reflect.ReflectUtil;
import sun.misc.Unsafe;

@SuppressWarnings("rawtypes")
public class CpuCachePadingRefence<T>
{
    protected long                                                                  p1, p2, p3, p4, p5, p6, p7;
    private int                                                                     p8;
    // 前后都有7个元素填充，可以保证该核心变量独自在一个缓存行中
    protected volatile T                                                            refence;
    protected long                                                                  p9, p10, p11, p12, p13, p14, p15;
    private static final UnsafeReferenceFieldUpdater<CpuCachePadingRefence, Object> updater       = new UnsafeReferenceFieldUpdater<CpuCachePadingRefence, Object>(CpuCachePadingRefence.class, "refence");
    private static final Unsafe                                                     unsafe        = ReflectUtil.getUnsafe();
    private static final long                                                       refenceOffset = ReflectUtil.getFieldOffset("refence", CpuCachePadingRefence.class);
    
    public CpuCachePadingRefence(T refence)
    {
        this.refence = refence;
    }
    
    public long shouleNotUse()
    {
        return p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + Long.valueOf((String) refence) + p9 + p10 + p11 + p12 + p13 + p14 + p15;
    }
    
    public void set(T newValue)
    {
        refence = newValue;
    }
    
    public void orderSet(T newValue)
    {
        updater.orderSet(this, newValue);
    }
    
    public T get()
    {
        return refence;
    }
    
    public boolean compareAndSwap(T expectedValue, T newValue)
    {
        return updater.compareAndSwap(this, expectedValue, newValue);
    }
    
    @SuppressWarnings("unchecked")
    public T getAndSet(T newValue)
    {
        return (T) updater.getAndSet(this, newValue);
    }
    
    public T setAndReturnOrigin(T newValue)
    {
        for (T t = refence;; t = refence)
        {
            if (unsafe.compareAndSwapObject(this, refenceOffset, t, newValue))
            {
                return t;
            }
        }
    }
}
