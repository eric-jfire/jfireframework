package com.jfireframework.baseutil.disruptor.waitstrategy;

import com.jfireframework.baseutil.disruptor.ringarray.RingArray;
import com.jfireframework.baseutil.disruptor.ringarray.RingArrayStopException;

public class BusyWaitStrategy implements WaitStrategy
{
    
    @Override
    public void waitFor(long next, RingArray array) throws RingArrayStopException
    {
        while (array.isAvailable(next) == false)
        {
            if (array.stoped())
            {
                throw RingArrayStopException.instance;
            }
        }
    }
    
    @Override
    public void signallBlockwaiting()
    {
    }
    
}
