package com.jfireframework.baseutil.disruptor.ringarray;

import com.jfireframework.baseutil.disruptor.EntryAction;
import com.jfireframework.baseutil.disruptor.waitstrategy.WaitStrategy;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class ComplexMultRingArray extends AbstractRingArray
{
    
    private final static int intShift;
    private final int[]      availableFlags;
                             
    static
    {
        int scale = Unsafe.ARRAY_INT_INDEX_SCALE;
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
        unsafe.putIntVolatile(availableFlags, Unsafe.ARRAY_INT_BASE_OFFSET + ((cursor & sizeMask) << intShift), (int) (cursor >>> flagShift));
        waitStrategy.signallBlockwaiting();
    }
    
    public boolean isAvailable(long cursor)
    {
        if ((int) (cursor >>> flagShift) == unsafe.getIntVolatile(availableFlags, Unsafe.ARRAY_INT_BASE_OFFSET + ((cursor & sizeMask) << intShift)))
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
        return add.next();
    }
    
}
