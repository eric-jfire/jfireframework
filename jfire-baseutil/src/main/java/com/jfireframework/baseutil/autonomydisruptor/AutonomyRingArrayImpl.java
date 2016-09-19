package com.jfireframework.baseutil.autonomydisruptor;

import java.util.concurrent.atomic.AtomicInteger;

import com.jfireframework.baseutil.concurrent.CpuCachePadingInt;
import com.jfireframework.baseutil.disruptor.Entry;
import com.jfireframework.baseutil.disruptor.EntryAction;
import com.jfireframework.baseutil.disruptor.Sequence;
import com.jfireframework.baseutil.disruptor.waitstrategy.WaitStrategy;
import com.jfireframework.baseutil.disruptor.waitstrategy.WaitStrategyStopException;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.verify.Verify;

import sun.misc.Unsafe;

public class AutonomyRingArrayImpl implements AutonomyRingArray
{
    protected WaitStrategy                   waitStrategy;
    protected volatile AutonomyEntryAction[] actions         = new AutonomyEntryAction[0];
    protected volatile int                   index           = 0;
    protected final Entry[]                  entries;
    // size是2的几次方幂，右移该数字相当于除操作
    protected final int                      flagShift;
    protected final int                      size;
    protected final int                      sizeMask;
    protected final Sequence                 cachedWrapPoint = new Sequence(Sequence.INIT_VALUE);
    // 代表下一个可以增加的位置
    protected final Sequence                 cursor          = new Sequence(Sequence.INIT_VALUE);
    protected static final Unsafe            unsafe          = ReflectUtil.getUnsafe();
    protected static final int               shift;
    // protected static final int BUFFER_PAD;
    protected static final int               REF_ARRAY_BASE;
    protected long                           p1, p2, p3, p4, p5, p6, p7;
    protected final AtomicInteger            threadNo        = new AtomicInteger(0);
    protected final CpuCachePadingInt        idleCount       = new CpuCachePadingInt(0);
    static
    {
        REF_ARRAY_BASE = unsafe.arrayBaseOffset(Entry[].class);
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
        // BUFFER_PAD = 128 / scale;
        // // Including the buffer pad in the array base offset
        // REF_ARRAY_BASE = unsafe.arrayBaseOffset(Entry[].class) + (BUFFER_PAD
        // << shift);
    }
    
    private final static int           intShift;
    private final static int           intOffset;
    private final int[]                availableFlags;
    private final CpuCachePadingInt[]  flags;
    protected long                     p11, p21, p31, p41, p51, p61, p71;
    protected static final int         BASE;
    protected static final int         SCALE;
    protected final EntryActionFactory entryActionFactory;
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
    
    public AutonomyRingArrayImpl(int size, WaitStrategy waitStrategy, EntryActionFactory factory)
    {
        Verify.True(size >= 1, "数组的大小必须大于1");
        Verify.True(Integer.bitCount(size) == 1, "数组的大小必须是2的次方幂");
        entries = new Entry[size];
        for (int i = 0; i < entries.length; i++)
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
        this.entryActionFactory = factory;
    }
    
    @Override
    public long next()
    {
        long next = cursor.next();
        long wrapPoint = cachedWrapPoint.value();
        if (next >= wrapPoint)
        {
            wrapPoint = getMax() + size;
            while (next >= wrapPoint)
            {
                wrapPoint = getMax() + size;
                checkForAddAction();
            }
            cachedWrapPoint.set(wrapPoint);
            checkForAddAction();
            return next;
        }
        else
        {
            checkForAddAction();
            return next;
        }
    }
    
    private void checkForAddAction()
    {
        int retry = 0;
        long t0 = System.nanoTime();
        while (retry < 10000)
        {
            if (idleCount.value() == 0)
            {
                retry += 1;
                continue;
            }
            else
            {
                return;
            }
        }
        long result = System.nanoTime() - t0;
        System.out.println("耗时：" + result);
        addAction();
    }
    
    @Override
    public synchronized boolean addAction()
    {
        if (index < actions.length)
        {
            AutonomyEntryAction entryAction = entryActionFactory.newEntryAction(this, getMax());
            idleCount.increaseAndGet();
            new Thread(entryAction, "autonomyDisruptor-action-thread-" + threadNo.incrementAndGet()).start();
            actions[index] = entryAction;
            index += 1;
            return true;
        }
        else
        {
            AutonomyEntryAction[] t_actions = new AutonomyEntryAction[actions.length + 1];
            System.arraycopy(actions, 0, t_actions, 0, actions.length);
            AutonomyEntryAction entryAction = entryActionFactory.newEntryAction(this, getMax());
            idleCount.increaseAndGet();
            new Thread(entryAction, "autonomyDisruptor-action-thread-" + threadNo.incrementAndGet()).start();
            t_actions[t_actions.length - 1] = entryAction;
            actions = t_actions;
            index = actions.length;
            System.out.println("增加");
            return true;
        }
    }
    
    @Override
    public boolean removeAction(AutonomyEntryAction action)
    {
        if (idleCount.decreaseAndGet() > 0)
        {
            synchronized (this)
            {
                int lastIndex = index - 1;
                index = lastIndex;
                actions[lastIndex].stop();
                actions[lastIndex] = null;
                System.out.println("删除");
                return true;
            }
        }
        else
        {
            idleCount.increaseAndGet();
            return false;
        }
    }
    
    /**
     * 获取所有的处理器中，最小的可以处理的序号。
     * 该序号意味着在循环数组上，放入的序号不可以覆盖该序号的内容
     * 
     * @return
     */
    public long getMax()
    {
        long max = 0;
        int lastIndex = index;
        for (int i = 0; i < lastIndex; i++)
        {
            EntryAction action = actions[i];
            if (action != null)
            {
                if (max < action.cursor())
                {
                    max = action.cursor();
                }
            }
        }
        return max;
    }
    
    @Override
    public Entry entryAt(long cursor)
    {
        // 不需要使用volatile读取。因为数组里的元素是不会变的
        return (Entry) unsafe.getObject(entries, REF_ARRAY_BASE + ((cursor & sizeMask) << shift));
    }
    
    @Override
    public boolean tryPublish(Object data)
    {
        boolean canPublish = true;
        final long current = cursor.value();
        final long next = current + 1;
        long wrapPoint = cachedWrapPoint.value();
        if (next >= wrapPoint)
        {
            wrapPoint = getMax() + size;
            if (next >= wrapPoint)
            {
                canPublish = false;
            }
            else
            {
                cachedWrapPoint.set(wrapPoint);
            }
        }
        if (canPublish)
        {
            if (cursor.tryCasSet(current, next))
            {
                Entry entry = entryAt(next);
                entry.setNewData(data);
                publish(next);
            }
            else
            {
                canPublish = false;
            }
        }
        return canPublish;
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
    
    @Override
    public void publish(long cursor)
    {
        // 下一步可能会使用volatile写入。所以这里只要使用ordered的写入方式，放入store-store屏障来保证上写不会重排序即可
        unsafe.putOrderedInt(availableFlags, intOffset + ((cursor & sizeMask) << intShift), (int) (cursor >>> flagShift));
        // ((CpuCachePadingInt) unsafe.getObject(flags, BASE + ((cursor &
        // sizeMask) <<intShift))).set((int) (cursor >>> flagShift));
        waitStrategy.signallBlockwaiting();
    }
    
    @Override
    public boolean isAvailable(long cursor)
    {
        if ((int) (cursor >>> flagShift) <= unsafe.getIntVolatile(availableFlags, intOffset + ((cursor & sizeMask) << intShift)))
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
    public CpuCachePadingInt idleCount()
    {
        return idleCount;
    }
    
}
