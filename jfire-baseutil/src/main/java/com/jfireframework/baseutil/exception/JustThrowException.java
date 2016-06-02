package com.jfireframework.baseutil.exception;

public class JustThrowException extends RuntimeException
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public JustThrowException(Throwable e)
    {
        super(e);
    }
    
    public JustThrowException(String msg, Throwable e)
    {
        super(msg, e);
    }
}
