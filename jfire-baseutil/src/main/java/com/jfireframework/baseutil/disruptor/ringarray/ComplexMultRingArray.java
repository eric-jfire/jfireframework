package com.jfireframework.baseutil.disruptor.ringarray;

import com.jfireframework.baseutil.concurrent.CpuCachePadingInt;
import com.jfireframework.baseutil.disruptor.EntryAction;
import com.jfireframework.baseutil.disruptor.waitstrategy.WaitStrategy;
import com.jfireframework.baseutil.reflect.ReflectUtil;

public final class ComplexMultRingArray extends AbstractMultRingArray
{
    
    private final static int          intShift;
    private final static int          intOffset;
    private final int[]               availableFlags;
    private final CpuCachePadingInt[] flags;
    protected long                    p1, p2, p3, p4, p5, p6, p7;
    protected static final int        BASE;
    protected static final int        SCALE;
    
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
        BASE = ReflectUtil.getUnsafe().arrayBaseOffset(CpuCachePadingInt[].class);
        SCALE = ReflectUtil.getUnsafe().arrayIndexScale(CpuCachePadingInt[].class);
        
    }
    
    public ComplexMultRingArray(int size, WaitStrategy waitStrategy, EntryAction[] actions)
    {
        super(size, waitStrategy, actions);
        availableFlags = new int[size];
        for (int i = 0, n = availableFlags.length; i < n; i++)
        {
            availableFlags[i] = -1;
        }
        flags = new CpuCachePadingInt[size];
        for (int i = 0, n = availableFlags.length; i < n; i++)
        {
            flags[i] = new CpuCachePadingInt(-1);
        }
    }
    
    public void publish(long cursor)
    {
        // 下一步可能会使用volatile写入。所以这里只要使用ordered的写入方式，放入store-store屏障来保证上写不会重排序即可
        unsafe.putOrderedInt(availableFlags, intOffset + ((cursor & sizeMask) << intShift), (int) (cursor >>> flagShift));
        // ((CpuCachePadingInt) unsafe.getObject(flags, BASE + ((cursor &
        // sizeMask) <<intShift))).set((int) (cursor >>> flagShift));
        waitStrategy.signallBlockwaiting();
    }
    
    public boolean isAvailable(long cursor)
    {
        if ((int) (cursor >>> flagShift) == unsafe.getIntVolatile(availableFlags, intOffset + ((cursor & sizeMask) << intShift)))
        // if ((int) (cursor >>> flagShift) == ((CpuCachePadingInt)
        // unsafe.getObject(flags, BASE + ((cursor & sizeMask)
        // <<intShift))).value())
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
