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
        unsafe.putIntVolatile(availableFlags, intOffset + ((cursor & sizeMask) << intShift), (int) (cursor >>> flagShift));
        waitStrategy.signallBlockwaiting();
    }
    
    public boolean isAvailable(long cursor)
    {
        if ((int) (cursor >>> flagShift) == unsafe.getIntVolatile(availableFlags, intOffset + ((cursor & sizeMask) << intShift)))
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
