package com.jfireframework.socket.test;

import com.jfireframework.baseutil.code.CodeLocation;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.DirectByteBuf;
import com.jfireframework.baseutil.collection.buffer.HeapByteBuf;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.jnet.common.exception.JnetException;
import com.jfireframework.jnet.common.handler.DataHandler;
import com.jfireframework.jnet.common.result.InternalResult;
import com.jfireframework.jnet.common.result.ServerInternalResult;

public class EchoHandler implements DataHandler
{
	private Logger logger = ConsoleLogFactory.getLogger(ConsoleLogFactory.DEBUG);
	
	@Override
	public Object handle(Object data, InternalResult entry) throws JnetException
	{
		ByteBuf<?> byteBuf = (ByteBuf<?>) data;
		ByteBuf<?> result = DirectByteBuf.allocate(100);
		result.addWriteIndex(4);
		result.put(byteBuf);
//		entry.setIndex(entry.getIndex() + 1);
		byteBuf.release();
		return result;
	}
	
	@Override
	public Object catchException(Object data, InternalResult result)
	{
		Throwable e = (Throwable) data;
		return data;
	}
	
}
