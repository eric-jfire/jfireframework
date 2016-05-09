package com.jfireframework.baseutil.disruptor.ringarray;

import com.jfireframework.baseutil.disruptor.Entry;
import com.jfireframework.baseutil.disruptor.waitstrategy.WaitStrategyStopException;

public interface RingArray
{
    
    public long next();
    
    public Entry entryAt(long cursor);
    
    public void publish(long cursor);
    
    public void publish(Object data);
    
    public boolean isAvailable(long cursor);
    
    public long cursor();
    
    public void waitFor(long cursor) throws WaitStrategyStopException;
    
    public void stop();
    
}
