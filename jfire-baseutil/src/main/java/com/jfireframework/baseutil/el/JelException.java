package com.jfireframework.baseutil.el;

public class JelException extends Exception
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public JelException(Throwable e)
    {
        super(e);
    }
    
    public JelException(String msg)
    {
        super(msg);
    }
}
