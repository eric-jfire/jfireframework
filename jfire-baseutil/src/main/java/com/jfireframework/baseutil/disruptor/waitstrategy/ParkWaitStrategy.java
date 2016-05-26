package com.jfireframework.baseutil.disruptor.waitstrategy;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;
import com.jfireframework.baseutil.disruptor.ringarray.RingArray;

public class ParkWaitStrategy extends AbstractWaitStrategy
{
    
    private final Thread[]      threads;
    private final AtomicInteger state = new AtomicInteger(0);
    
    public ParkWaitStrategy(Thread[] threads)
    {
        this.threads = threads;
    }
    
    @Override
    public void waitFor(long next, RingArray ringArray) throws WaitStrategyStopException
    {
        if (ringArray.isAvailable(next))
        {
            return;
        }
        while (ringArray.isAvailable(next) == false)
        {
            if (state.get() == 0)
            {
                if (state.compareAndSet(0, 1))
                {
                    if (ringArray.isAvailable(next))
                    {
                        state.set(0);
                        for (Thread each : threads)
                        {
                            LockSupport.unpark(each);
                        }
                        detectStopException();
                        break;
                    }
                }
            }
            LockSupport.park();
            detectStopException();
        }
    }
    
    @Override
    public void signallBlockwaiting()
    {
        if (state.get() == 1)
        {
            if (state.compareAndSet(1, 0))
            {
                for (Thread each : threads)
                {
                    LockSupport.unpark(each);
                }
            }
        }
        
    }
    
}
