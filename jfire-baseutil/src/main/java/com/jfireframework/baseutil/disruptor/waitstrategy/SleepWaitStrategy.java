package com.jfireframework.baseutil.disruptor.waitstrategy;

import java.util.concurrent.locks.LockSupport;
import com.jfireframework.baseutil.disruptor.ringarray.RingArray;

public class SleepWaitStrategy extends AbstractWaitStrategy
{
    private final long sleepNanos;
    
    public SleepWaitStrategy(long sleepNanos)
    {
        this.sleepNanos = sleepNanos;
    }
    
    @Override
    public void waitFor(long next, RingArray array) throws WaitStrategyStopException
    {
        while (array.isAvailable(next) == false)
        {
            detectStopException();
            LockSupport.parkNanos(sleepNanos);
        }
    }
    
    @Override
    public void signallBlockwaiting()
    {
    }
}
