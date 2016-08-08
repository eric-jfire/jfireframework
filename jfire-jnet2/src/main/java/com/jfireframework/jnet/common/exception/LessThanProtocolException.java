package com.jfireframework.jnet.common.exception;

/**
 * 读取的长度不满足一个报文协议长度
 * 
 * @author eric(eric@jfire.cn)
 *
 */
public class LessThanProtocolException extends JnetException
{
	
	/**
	 * 
	 */
	private static final long						serialVersionUID	= 4785438471276493258L;
	public static final LessThanProtocolException	instance			= new LessThanProtocolException();
	
	private LessThanProtocolException()
	{
		
	}
	
}
