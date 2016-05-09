package com.jfireframework.baseutil.disruptor.waitstrategy;

import com.jfireframework.baseutil.disruptor.ringarray.RingArray;

public interface WaitStrategy
{
    public void waitFor(long next, RingArray ringArray) throws WaitStrategyStopException;
    
    /**
     * 唤醒所有在等待MessageRingArray可用的处理器
     */
    public void signallBlockwaiting();
    
    /**
     * 检测是否已经有停止异常被抛出
     */
    public void detectStopException();
    
    /**
     * 停止所有的的运行或者是等待
     */
    public void stopRunOrWait();
    
}
