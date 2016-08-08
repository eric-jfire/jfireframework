package com.jfireframework.socket.test;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.HeapByteBuf;
import com.jfireframework.baseutil.collection.buffer.HeapByteBufPool;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.jnet2.common.decodec.FrameDecodec;
import com.jfireframework.jnet2.common.exception.BufNotEnoughException;
import com.jfireframework.jnet2.common.exception.LessThanProtocolException;
import com.jfireframework.jnet2.common.exception.NotFitProtocolException;

public class LengthFrameDecodec implements FrameDecodec
{
	private Logger logger = ConsoleLogFactory.getLogger(ConsoleLogFactory.DEBUG);
	
	@Override
	public ByteBuf<?> decodec(ByteBuf<?> ioBuffer) throws NotFitProtocolException, BufNotEnoughException, LessThanProtocolException
	{
		logger.debug(ioBuffer.toString());
		if (ioBuffer.remainRead() == 0)
		{
			return null;
		}
		int length = ioBuffer.get();
		if (ioBuffer.remainRead() >= length)
		{
			HeapByteBuf result = HeapByteBufPool.getInstance().get(length);
			result.put(ioBuffer, length);
			ioBuffer.addReadIndex(length);
			return result;
		}
		else
		{
			return null;
		}
	}
	
}
