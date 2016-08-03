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
