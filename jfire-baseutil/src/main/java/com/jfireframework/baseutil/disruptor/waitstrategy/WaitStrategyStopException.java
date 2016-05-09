package com.jfireframework.baseutil.disruptor.waitstrategy;

public class WaitStrategyStopException extends RuntimeException
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private WaitStrategyStopException()
    {
    }
    
    public static final WaitStrategyStopException instance = new WaitStrategyStopException();
}
