package com.jfireframework.jnet.common.exception;

public class JnetException extends Exception
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1011661639624817862L;
	
	public JnetException()
	{
		
	}
	
	public JnetException(String msg)
	{
		super(msg);
	}
	
	public JnetException(String msg, Throwable e)
	{
		super(msg, e);
	}
	
	/**
	 * 覆盖原有的jdk方法，不再重新填充堆栈信息，这样就保证了性能
	 */
	public Throwable fillInStackTrace()
	{
		return this;
	}
}
