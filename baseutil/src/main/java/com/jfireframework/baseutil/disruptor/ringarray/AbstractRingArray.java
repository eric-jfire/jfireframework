package com.jfireframework.baseutil.disruptor.ringarray;

import java.util.concurrent.locks.LockSupport;
import com.jfireframework.baseutil.disruptor.CpuCachePadingValue;
import com.jfireframework.baseutil.disruptor.Entry;
import com.jfireframework.baseutil.disruptor.EntryAction;
import com.jfireframework.baseutil.disruptor.waitstrategy.WaitStrategy;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.verify.Verify;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public abstract class AbstractRingArray implements RingArray
{
    protected final WaitStrategy  waitStrategy;
    protected final EntryAction[] actions;
    protected final Entry[]       entries;
    // size是2的几次方幂，右移该数字相当于除操作
    protected final int           flagShift;
    protected final int           size;
    protected final int           sizeMask;
    protected volatile long       wrapPoint;
    protected volatile int        state  = 0;
    // 代表下一个可以增加的位置
    protected CpuCachePadingValue add    = new CpuCachePadingValue();
    protected static Unsafe       unsafe = ReflectUtil.getUnsafe();
    protected static int          STOPED = 1;
    protected static long         base;
    protected static int          shift;
                                  
    static
    {
        base = unsafe.arrayBaseOffset(Entry[].class);
        int scale = unsafe.arrayIndexScale(Entry[].class);
        if (scale == 8)
        {
            shift = 3;
        }
        else if (scale == 4)
        {
            shift = 2;
        }
        else
        {
            throw new RuntimeException("不认识");
        }
    }
    
    public AbstractRingArray(int size, WaitStrategy waitStrategy, EntryAction[] actions)
    {
        Verify.True(size > 1, "数组的大小必须大于1");
        Verify.True(Integer.bitCount(size) == 1, "数组的大小必须是2的次方幂");
        entries = new Entry[size];
        for (int i = 0; i < size; i++)
        {
            entries[i] = new Entry();
        }
        this.actions = actions;
        this.size = size;
        sizeMask = size - 1;
        this.waitStrategy = waitStrategy;
        wrapPoint = size - 1;
        flagShift = Integer.numberOfTrailingZeros(size);
    }
    
    @Override
    public long next()
    {
        long next = getNext();
        do
        {
            if (next >= wrapPoint)
            {
                LockSupport.parkNanos(1);
                wrapPoint = getMin() + size;
            }
            else
            {
                return next;
            }
        } while (true);
    }
    
    protected abstract long getNext();
    
    private long getMin()
    {
        long min = Long.MAX_VALUE;
        for (EntryAction each : actions)
        {
            if (min > each.cursor())
            {
                min = each.cursor();
            }
        }
        return min;
    }
    
    @Override
    public Entry entryAt(long cursor)
    {
        return (Entry) unsafe.getObjectVolatile(entries, base + ((cursor & sizeMask) << shift));
    }
    
    @Override
    public void publish(Object data)
    {
        if (state != STOPED)
        {
            long cursor = next();
            Entry entry = entryAt(cursor);
            entry.setNewData(data);
            publish(cursor);
        }
        else
        {
            throw RingArrayStopException.instance;
        }
    }
    
    @Override
    public long cursor()
    {
        return add.getPoint();
    }
    
    @Override
    public void waitFor(long cursor) throws RingArrayStopException
    {
        waitStrategy.waitFor(cursor, this);
    }
    
    @Override
    public void stop()
    {
        state = STOPED;
        waitStrategy.signallBlockwaiting();
    }
    
    @Override
    public boolean stoped()
    {
        return state == STOPED;
    }
    
}
