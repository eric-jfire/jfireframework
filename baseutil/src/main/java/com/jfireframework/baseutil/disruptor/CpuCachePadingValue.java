package com.jfireframework.baseutil.disruptor;

import com.jfireframework.baseutil.reflect.ReflectUtil;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
class LeftPad
{
    protected long p1, p2, p3, p4, p5, p6, p7;
    
}

class RealValue extends LeftPad
{
    protected volatile long point = -1; // 前后都有7个元素填充，可以保证该核心变量独自在一个缓存行中
}

class RightPad extends RealValue
{
    protected long p9, p10, p11, p12, p13, p14, p15;
}

@SuppressWarnings("restriction")
public class CpuCachePadingValue extends RightPad
{
    
    private static long   offset = ReflectUtil.getFieldOffset("point", RealValue.class);
    private static Unsafe unsafe = ReflectUtil.getUnsafe();
                                 
    public CpuCachePadingValue()
    {
    }
    
    public CpuCachePadingValue(long initValue)
    {
        point = initValue;
    }
    
    /**
     * 获取下一个可用的位置
     * 
     * @return
     */
    public long next()
    {
        while (true)
        {
            long now = point;
            long next = now + 1;
            if (unsafe.compareAndSwapLong(this, offset, now, next))
            {
                return next;
            }
        }
    }
    
    public void set(long point)
    {
        this.point = point;
    }
    
    public boolean tryCasSet(long now, long target)
    {
        return unsafe.compareAndSwapLong(this, offset, now, target);
    }
    
    /**
     * cas设置值。由于可能存在比较厉害的竞争，所以失败时休眠1纳秒。
     * 这个方法应用于RingArray的publish中，所以休眠1纳秒问题不会很大
     * 
     * @param now
     * @param target
     */
    public void casSet(long now, long target)
    {
        while (unsafe.compareAndSwapLong(this, offset, now, target) == false)
        {
        }
    }
    
    public long getPoint()
    {
        return point;
    }
    
    /**
     * 这个方法没有实际的作用，目的是为了避免jvm进行代码优化的时候将无用的p0-p6属性优化掉
     * 
     * @return
     */
    protected long nouse()
    {
        return p1 + p2 + p3 + p4 + p5 + p6 + p7 + point + p9 + p10 + p11 + p12 + p13 + p14 + p15;
    }
}
