package com.jfireframework.baseutil.disruptor.ringarray;

import com.jfireframework.baseutil.disruptor.EntryAction;
import com.jfireframework.baseutil.disruptor.waitstrategy.WaitStrategy;
import com.jfireframework.baseutil.reflect.ReflectUtil;

public class ComplexMultRingArray extends AbstractMultRingArray
{
    
    private final static int intShift;
    private final static int intOffset;
    private final int[]      availableFlags;
    
    static
    {
        int scale = ReflectUtil.getUnsafe().arrayIndexScale(int[].class);
        intOffset = ReflectUtil.getUnsafe().arrayBaseOffset(int[].class);
        if (scale == 8)
        {
            intShift = 3;
        }
        else if (scale == 4)
        {
            intShift = 2;
        }
        else
        {
            throw new RuntimeException("不认识");
        }
    }
    
    public ComplexMultRingArray(int size, WaitStrategy waitStrategy, EntryAction[] actions)
    {
        super(size, waitStrategy, actions);
        availableFlags = new int[size];
        for (int i = 0, n = availableFlags.length; i < n; i++)
        {
            availableFlags[i] = -1;
        }
    }
    
    public void publish(long cursor)
    {
        // 下一步可能会使用volatile写入。所以这里只要使用ordered的写入方式，放入store-store屏障来保证上写不会重排序即可
        unsafe.putOrderedInt(availableFlags, intOffset + ((cursor & sizeMask) << intShift), (int) (cursor >>> flagShift));
        waitStrategy.signallBlockwaiting();
    }
    
    public boolean isAvailable(long cursor)
    {
        /*
         * 在这里必须要使用<=这个符号。
         * 之前使用==出现了错误。假设队列容量不大，消费者线程比较多。
         * 如果有消费者线程被唤醒之后还没尝试处理数据前，别的消费者线程全部处理了数据就会导致整个flag数组序号递增。
         * 进而导致该线程永远都无法判断==结果。
         * 就会导致一个消费者线程永久性的失去作用。
         */
        if ((int) (cursor >>> flagShift) <= unsafe.getIntVolatile(availableFlags, intOffset + ((cursor & sizeMask) << intShift)))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    @Override
    protected long getNext()
    {
        return cursor.next();
    }
    
}
