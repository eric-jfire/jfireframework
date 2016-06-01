package com.jfireframework.baseutil.disruptor;

import com.jfireframework.baseutil.reflect.ReflectUtil;
import sun.misc.Unsafe;

class LeftPad
{
    protected long p1, p2, p3, p4, p5, p6, p7;
    
}

class RealValue extends LeftPad
{
    protected volatile long value = -1; // 前后都有7个元素填充，可以保证该核心变量独自在一个缓存行中
}

class RightPad extends RealValue
{
    protected long p9, p10, p11, p12, p13, p14, p15;
}

/**
 * 一个经过了缓存行填充后的序号。
 * 
 * @author Administrator
 *
 */
public class Sequence extends RightPad
{
    
    private static long      offset     = ReflectUtil.getFieldOffset("value", RealValue.class);
    private static Unsafe    unsafe     = ReflectUtil.getUnsafe();
    public static final long INIT_VALUE = -1;
    
    public Sequence()
    {
        this(INIT_VALUE);
    }
    
    public Sequence(long initValue)
    {
        value = initValue;
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
            long now = value;
            long next = now + 1;
            if (unsafe.compareAndSwapLong(this, offset, now, next))
            {
                return next;
            }
        }
    }
    
    public void orderedSet(long value)
    {
        unsafe.putOrderedLong(this, offset, value);
    }
    
    public void set(long point)
    {
        this.value = point;
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
    
    public long value()
    {
        return value;
    }
    
    /**
     * 这个方法没有实际的作用，目的是为了避免jvm进行代码优化的时候将无用的p0-p6属性优化掉
     * 
     * @return
     */
    protected long nouse()
    {
        return p1 + p2 + p3 + p4 + p5 + p6 + p7 + value + p9 + p10 + p11 + p12 + p13 + p14 + p15;
    }
}
