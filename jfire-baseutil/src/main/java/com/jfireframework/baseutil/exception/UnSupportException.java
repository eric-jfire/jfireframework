package com.jfireframework.baseutil.exception;

public class UnSupportException extends RuntimeException
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public UnSupportException(String msg)
    {
        super(msg);
    }
    
    public UnSupportException(String msg, Throwable e)
    {
        super(msg, e);
    }
}
