package com.jfireframework.baseutil.disruptor.ringarray;

import java.util.concurrent.locks.LockSupport;
import com.jfireframework.baseutil.disruptor.Sequence;
import com.jfireframework.baseutil.disruptor.Entry;
import com.jfireframework.baseutil.disruptor.EntryAction;
import com.jfireframework.baseutil.disruptor.waitstrategy.WaitStrategyStopException;
import com.jfireframework.baseutil.disruptor.waitstrategy.WaitStrategy;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.verify.Verify;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public abstract class AbstractMultRingArray implements RingArray
{
    protected final WaitStrategy  waitStrategy;
    protected final EntryAction[] actions;
    protected final Entry[]       entries;
    // size是2的几次方幂，右移该数字相当于除操作
    protected final int           flagShift;
    protected final int           size;
    protected final int           sizeMask;
    protected final Sequence      cachedWrapPoint = new Sequence(Sequence.INIT_VALUE);
    // 代表下一个可以增加的位置
    protected final Sequence      cursor          = new Sequence(Sequence.INIT_VALUE);
    protected static final Unsafe unsafe          = ReflectUtil.getUnsafe();
    protected static final long   base;
    protected static final int    shift;
    
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
    
    public AbstractMultRingArray(int size, WaitStrategy waitStrategy, EntryAction[] actions)
    {
        Verify.True(size > 1, "数组的大小必须大于1");
        Verify.True(Integer.bitCount(size) == 1, "数组的大小必须是2的次方幂");
        entries = new Entry[size];
        for (int i = 0; i < size; i++)
        {
            entries[i] = new Entry();
        }
        this.actions = actions;
        for (EntryAction each : actions)
        {
            each.setRingArray(this);
        }
        this.size = size;
        sizeMask = size - 1;
        this.waitStrategy = waitStrategy;
        cachedWrapPoint.set(size);
        flagShift = Integer.numberOfTrailingZeros(size);
    }
    
    @Override
    public long next()
    {
        long next = getNext();
        long wrapPoint = cachedWrapPoint.value();
        if (next >= wrapPoint)
        {
            wrapPoint = getMin() + size;
            while (next >= wrapPoint)
            {
                LockSupport.parkNanos(1);
                wrapPoint = getMin() + size;
            }
            cachedWrapPoint.set(wrapPoint);
            return next;
        }
        else
        {
            return next;
        }
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
        // 不需要使用volatile读取。因为数组里的元素是不会变的
        return (Entry) unsafe.getObject(entries, base + ((cursor & sizeMask) << shift));
    }
    
    @Override
    public void publish(Object data)
    {
        long cursor = next();
        Entry entry = entryAt(cursor);
        entry.setNewData(data);
        publish(cursor);
    }
    
    @Override
    public long cursor()
    {
        return cursor.value();
    }
    
    @Override
    public void waitFor(long cursor) throws WaitStrategyStopException
    {
        waitStrategy.waitFor(cursor, this);
    }
    
    @Override
    public void stop()
    {
        waitStrategy.stopRunOrWait();
    }
    
}
