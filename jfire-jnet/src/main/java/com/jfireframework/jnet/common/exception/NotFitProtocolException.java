package com.jfireframework.jnet.common.exception;

/**
 * 表明读取的数据不符合报文协议
 * 
 * @author 林斌
 * 
 */
public class NotFitProtocolException extends JnetException
{
	
	/**
	 * 
	 */
	private static final long					serialVersionUID	= -246003536673386746L;
	public static final NotFitProtocolException	instance			= new NotFitProtocolException();
	
	private NotFitProtocolException()
	{
	}
	
}
