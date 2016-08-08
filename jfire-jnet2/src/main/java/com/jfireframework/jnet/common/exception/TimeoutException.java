package com.jfireframework.jnet.common.exception;

public class TimeoutException extends JnetException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1286997209432696734L;
	
	public TimeoutException(long timeout)
	{
		super("等待服务器返回结果超时,当前客户端设置超时时间为" + timeout + "毫秒");
	}
}
