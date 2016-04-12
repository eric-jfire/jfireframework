package com.jfireframework.baseutil.disruptor.waitstrategy;

import com.jfireframework.baseutil.disruptor.ringarray.RingArray;
import com.jfireframework.baseutil.disruptor.ringarray.RingArrayStopException;

public class CirculationWaitStrategy implements WaitStrategy
{
    private final int count;
    
    public CirculationWaitStrategy(int count)
    {
        this.count = count;
    }
    
    @Override
    public void waitFor(long next, RingArray ringArray) throws RingArrayStopException
    {
        while (ringArray.isAvailable(next) == false)
        {
            if (ringArray.stoped())
            {
                throw RingArrayStopException.instance;
            }
            for (int i = 0; i < count; i++)
                ;
        }
    }
    
    @Override
    public void signallBlockwaiting()
    {
        // TODO Auto-generated method stub
        
    }
    
}
