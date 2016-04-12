package com.jfireframework.baseutil.disruptor.waitstrategy;

import java.util.concurrent.locks.LockSupport;
import com.jfireframework.baseutil.disruptor.ringarray.RingArray;
import com.jfireframework.baseutil.disruptor.ringarray.RingArrayStopException;

public class SleepWaitStrategy implements WaitStrategy
{
    private final long sleepNanos;
    
    public SleepWaitStrategy(long sleepNanos)
    {
        this.sleepNanos = sleepNanos;
    }
    
    @Override
    public void waitFor(long next, RingArray array) throws RingArrayStopException
    {
        while (array.isAvailable(next) == false)
        {
            if (array.stoped())
            {
                throw RingArrayStopException.instance;
            }
            LockSupport.parkNanos(sleepNanos);
        }
    }
    
    @Override
    public void signallBlockwaiting()
    {
    }
}
