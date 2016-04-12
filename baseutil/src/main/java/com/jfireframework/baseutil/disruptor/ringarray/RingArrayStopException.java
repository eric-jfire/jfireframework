package com.jfireframework.baseutil.disruptor.ringarray;

public class RingArrayStopException extends RuntimeException
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private RingArrayStopException()
    {
    }
    
    public static final RingArrayStopException instance = new RingArrayStopException();
}
