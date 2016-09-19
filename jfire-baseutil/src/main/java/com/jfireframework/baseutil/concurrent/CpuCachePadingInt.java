package com.jfireframework.baseutil.concurrent;

import com.jfireframework.baseutil.reflect.ReflectUtil;
import sun.misc.Unsafe;

public class CpuCachePadingInt
{
    protected long              p1, p2, p3, p4, p5, p6, p7;
    private int                 p8;
    // 前后都有7个元素填充，可以保证该核心变量独自在一个缓存行中
    protected volatile int      value;
    protected long              p9, p10, p11, p12, p13, p14, p15;
    private static final long   offset = ReflectUtil.getFieldOffset("value", CpuCachePadingInt.class);
    private static final Unsafe unsafe = ReflectUtil.getUnsafe();
    
    public CpuCachePadingInt(int initValue)
    {
        value = initValue;
    }
    
    public long shouleNotUse()
    {
        return p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + value + p9 + p10 + p11 + p12 + p13 + p14 + p15;
    }
    
    public void set(int newValue)
    {
        value = newValue;
    }
    
    public void orderSet(int newValue)
    {
        
    }
    
    public int value()
    {
        return value;
    }
    
    public boolean compareAndSwap(int expectedValue, int newValue)
    {
        return unsafe.compareAndSwapInt(this, offset, expectedValue, newValue);
    }
    
    public int increaseAndGet()
    {
        do
        {
            int current = value;
            int newValue = current + 1;
            if (unsafe.compareAndSwapInt(this, offset, current, newValue))
            {
                return newValue;
            }
        } while (true);
    }
    
    public int decreaseAndGet()
    {
        do
        {
            int current = value;
            int newValue = current - 1;
            if (unsafe.compareAndSwapInt(this, offset, current, newValue))
            {
                return newValue;
            }
        } while (true);
    }
    
    public int getAndSet(int newValue)
    {
        while (true)
        {
            int current = value;
            if (unsafe.compareAndSwapInt(this, offset, current, newValue))
            {
                return current;
            }
        }
    }
}
