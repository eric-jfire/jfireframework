package com.jfireframework.jnet2.common.decodec;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.jnet2.common.exception.BufNotEnoughException;
import com.jfireframework.jnet2.common.exception.LessThanProtocolException;
import com.jfireframework.jnet2.common.exception.NotFitProtocolException;

public interface FrameDecodec
{
	/**
	 * 报文解码器。从参数的ByteBuf中分离出来一个完整的报文。该接口的实现对于返回值应该新建一个新的Bytebuf
	 * 
	 * @param ioBuffer
	 * @return
	 * @throws NotFitProtocolException
	 * @throws BufNotEnoughException
	 * @throws LessThanProtocolException
	 */
	public ByteBuf<?> decodec(ByteBuf<?> ioBuffer) throws NotFitProtocolException, BufNotEnoughException, LessThanProtocolException;
}
