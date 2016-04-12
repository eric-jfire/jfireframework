package com.jfireframework.rpc.exception;

public class InvokeException extends LbrcException
{
	
	/**
     * 
     */
	private static final long serialVersionUID = 2837996250964523824L;
	
	public InvokeException(String msg, Throwable e)
	{
		super(msg, e);
	}
	
	public InvokeException(String string)
	{
		super(string);
	}
	
}
