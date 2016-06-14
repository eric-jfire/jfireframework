package com.jfireframework.baseutil.el;

public class ElException extends Exception
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public ElException(Throwable e)
    {
        super(e);
    }
    
    public ElException(String msg)
    {
        super(msg);
    }
}
