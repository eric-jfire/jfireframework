package com.jfireframework.baseutil.disruptor.waitstrategy;

import com.jfireframework.baseutil.disruptor.ringarray.RingArray;

public class CirculationWaitStrategy extends AbstractWaitStrategy
{
    private final int count;
    
    public CirculationWaitStrategy(int count)
    {
        this.count = count;
    }
    
    @Override
    public void waitFor(long next, RingArray ringArray) throws WaitStrategyStopException
    {
        while (ringArray.isAvailable(next) == false)
        {
            detectStopException();
            for (int i = 0; i < count; i++)
            {
                ;
            }
        }
    }
    
    @Override
    public void signallBlockwaiting()
    {
        
    }
    
}
