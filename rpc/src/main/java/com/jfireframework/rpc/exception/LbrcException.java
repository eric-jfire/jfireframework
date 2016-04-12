package com.jfireframework.rpc.exception;

public class LbrcException extends RuntimeException
{
    protected String          msg              = null;
    /**
     * 
     */
    private static final long serialVersionUID = 8918888704235432212L;
    
    public LbrcException(String msg, Throwable e)
    {
        super(msg, e);
        this.msg = msg;
    }
    
    public LbrcException(Throwable e)
    {
        super(e);
        msg = e.getMessage();
    }
    
    public LbrcException(String msg)
    {
        super(msg);
        this.msg = msg;
    }
    
    public String toString()
    {
        return msg;
    }
}
