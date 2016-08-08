package com.jfireframework.jnet.common.exception;

public class EndOfStreamException extends JnetException
{
    
    /**
     * 
     */
    private static final long                serialVersionUID = 8037997970885790653L;
    public static final EndOfStreamException instance         = new EndOfStreamException();
    
    private EndOfStreamException()
    {
        
    }
}
