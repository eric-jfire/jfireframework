package com.jfireframework.baseutil.disruptor.waitstrategy;

import java.util.concurrent.locks.LockSupport;
import com.jfireframework.baseutil.concurrent.CpuCachePadingInt;
import com.jfireframework.baseutil.disruptor.ringarray.RingArray;

public class ParkWaitStrategy extends AbstractWaitStrategy
{
    
    private final Thread[]          threads;
    private final static int        WORK     = 0;
    private final static int        PARKED   = 1;
    private final CpuCachePadingInt parkFlag = new CpuCachePadingInt(WORK);
    
    public ParkWaitStrategy(Thread[] threads)
    {
        this.threads = threads;
    }
    
    @Override
    public void waitFor(long next, RingArray ringArray) throws WaitStrategyStopException
    {
        while (ringArray.isAvailable(next) == false)
        {
            /**
             * 这里在cas成功后需要唤醒所有的线程。如果不唤醒会出现一个bug场景导致活锁。
             * 线程1序号很小，比当前cursor小一个ringsize。线程2序号比当前cursor大1.
             * 线程1cas失败直接挂起。线程2cas成功后recheck也挂起。生产者由于cursor比线程1的序号大一个ringsize，也无法放入数据。
             * 导致活锁。
             */
            if (parkFlag.value() == WORK && parkFlag.compareAndSwap(WORK, PARKED))
            {
                for (Thread each : threads)
                {
                    LockSupport.unpark(each);
                }
                continue;
            }
            LockSupport.park();
            detectStopException();
        }
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
