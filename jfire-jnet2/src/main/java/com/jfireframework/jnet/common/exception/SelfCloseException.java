package com.jfireframework.jnet.common.exception;

/**
 * 如果程序自己关闭连接并没有什么异常的时候，就选择这个异常
 * 
 * @author eric(eric@jfire.cn)
 *
 */
public class SelfCloseException extends JnetException
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5549905347605918610L;
	
	public SelfCloseException()
	{
		super("代码己关闭了连接");
	}
}
