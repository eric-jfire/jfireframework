package com.jfireframework.baseutil.disruptor.waitstrategy;

import com.jfireframework.baseutil.disruptor.ringarray.RingArray;
import com.jfireframework.baseutil.disruptor.ringarray.RingArrayStopException;

public interface WaitStrategy
{
    public void waitFor(long next, RingArray ringArray) throws RingArrayStopException;
    
    /**
     * 唤醒所有在等待MessageRingArray可用的处理器
     */
    public void signallBlockwaiting();
    
}
