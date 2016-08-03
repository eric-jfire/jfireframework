package com.jfireframework.baseutil.disruptor.waitstrategy;

import java.util.concurrent.locks.LockSupport;
import com.jfireframework.baseutil.concurrent.CpuCachePadingInt;
import com.jfireframework.baseutil.disruptor.EntryAction;
import com.jfireframework.baseutil.disruptor.ringarray.RingArray;

public class ParkWaitStrategy extends AbstractWaitStrategy
{
    
    private final Thread[]          threads;
    private final static int        WORK     = 0;
    private final static int        PARKED   = 1;
    private final CpuCachePadingInt parkFlag = new CpuCachePadingInt(WORK);
    private final EntryAction[]     actions;
    
    public ParkWaitStrategy(Thread[] threads, EntryAction[] actions)
    {
        this.actions = actions;
        this.threads = threads;
    }
    
    @Override
    public void waitFor(long next, RingArray ringArray) throws WaitStrategyStopException
    {
        while (ringArray.isAvailable(next) == false)
        {
            if (parkFlag.value() == WORK)
            {
                if (parkFlag.compareAndSwap(WORK, PARKED))
                {
                    // 这边在自身判断失败的情况，还要帮助最小的号码进行一次判断。否则会出现如下的死锁路径
                    // 线程1序号小，在第二个if处失败，进入park。线程2序号大，刚好大一个ringarray的size，就会在第三个if的第一个判断处失败。
                    // 而在那之前，消费者因为状态是work所以没有唤醒能力。
                    // 如果此时线程2也进入睡眠，那么再也没有程序可以帮助执行唤醒了。因为线程1卡主了，没有生产者可以再次放入数据。
                    // 所以这里在判断自己失败后，还要帮忙判断下别人是不是也失败了
                    if (ringArray.isAvailable(next))
                    {
                        parkFlag.set(WORK);
                        for (Thread each : threads)
                        {
                            LockSupport.unpark(each);
                        }
                        break;
                    }
                    else if (ringArray.isAvailable(getMinNext()))
                    {
                        parkFlag.set(WORK);
                        for (Thread each : threads)
                        {
                            LockSupport.unpark(each);
                        }
                        continue;
                    }
                }
            }
            LockSupport.park();
            detectStopException();
        }
    }
    
    private long getMinNext()
    {
        long min = Long.MAX_VALUE;
        for (EntryAction action : actions)
        {
            if (min > action.cursor())
            {
                min = action.cursor();
            }
        }
        return min;
    }
    
    @Override
    public void signallBlockwaiting()
    {
        if (parkFlag.value() == PARKED && parkFlag.compareAndSwap(PARKED, WORK))
        {
            for (Thread each : threads)
            {
                LockSupport.unpark(each);
            }
        }
        
    }
    
}
