package com.jfireframework.socket.test;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.jnet.common.exception.JnetException;
import com.jfireframework.jnet.common.handler.DataHandler;
import com.jfireframework.jnet.common.result.InternalResult;

public class Loghandler implements DataHandler
{
	private Logger logger = ConsoleLogFactory.getLogger(ConsoleLogFactory.DEBUG);
	
	@Override
	public Object handle(Object data, InternalResult result) throws JnetException
	{
		ByteBuf<?> byteBuf = (ByteBuf<?>) data;
		logger.debug("当前buffer状态{}", byteBuf.toString());
		// result.setData(data);
		// result.setIndex(1);
		// result.getChannelInfo().turnToWorkthreadHandle(result);
		// return DataHandler.skipToWorkRing;
		return data;
	}
	
	@Override
	public Object catchException(Object data, InternalResult result)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
}
