package com.jfireframework.baseutil.disruptor.waitstrategy;

import com.jfireframework.baseutil.disruptor.ringarray.RingArray;

public class YieldStrategy extends AbstractWaitStrategy
{
    @Override
    public void waitFor(long next, RingArray array) throws WaitStrategyStopException
    {
        while (array.isAvailable(next) == false)
        {
            detectStopException();
            Thread.yield();
        }
    }
    
    @Override
    public void signallBlockwaiting()
    {
    }
}
