package com.jfireframework.baseutil.concurrent;

import com.jfireframework.baseutil.reflect.ReflectUtil;
import sun.misc.Unsafe;

public class CpuCachePadingLong
{
    protected long              p1, p2, p3, p4, p5, p6, p7;
    // 前后都有7个元素填充，可以保证该核心变量独自在一个缓存行中
    protected volatile long     value;
    protected long              p9, p10, p11, p12, p13, p14, p15;
    private static final long   offset = ReflectUtil.getFieldOffset("value", CpuCachePadingInt.class);
    private static final Unsafe unsafe = ReflectUtil.getUnsafe();
    
    public CpuCachePadingLong(long initValue)
    {
        value = initValue;
    }
    
    public long shouleNotUse()
    {
        return p1 + p2 + p3 + p4 + p5 + p6 + p7 + value + p9 + p10 + p11 + p12 + p13 + p14 + p15;
    }
    
    public void set(long newValue)
    {
        value = newValue;
    }
    
    public void orderSet(int newValue)
    {
        
    }
    
    public long value()
    {
        return value;
    }
    
    public boolean compareAndSwap(int expectedValue, int newValue)
    {
        return unsafe.compareAndSwapInt(this, offset, expectedValue, newValue);
    }
    
    public long getAndSet(int newValue)
    {
        while (true)
        {
            long current = value;
            if (unsafe.compareAndSwapLong(this, offset, current, newValue))
            {
                return current;
            }
        }
    }
}
