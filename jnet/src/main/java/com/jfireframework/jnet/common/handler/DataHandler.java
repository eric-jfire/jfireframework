package com.jfireframework.jnet.common.handler;

import com.jfireframework.jnet.common.exception.JnetException;
import com.jfireframework.jnet.common.result.InternalResult;

public interface DataHandler
{
	public final Object skipToWorkRing = new Object();
	
	/**
	 * 对传递过来的数据做处理。并且将处理完成的结果返回。后续的处理器会继续处理这个对象
	 * 
	 * @param data
	 * @param entry
	 * @throws Exception
	 */
	public Object handle(Object data, InternalResult result) throws JnetException;
	
	/**
	 * 通道发生异常是，处理链上的该方法会被调用
	 * 
	 * @param data
	 * @param result
	 * @return
	 */
	public Object catchException(Object data, InternalResult result);
	
}
