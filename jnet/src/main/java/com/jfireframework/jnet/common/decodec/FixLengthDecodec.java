package com.jfireframework.jnet.common.decodec;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.DirectByteBufPool;
import com.jfireframework.jnet.common.exception.BufNotEnoughException;
import com.jfireframework.jnet.common.exception.LessThanProtocolException;
import com.jfireframework.jnet.common.exception.NotFitProtocolException;

public class FixLengthDecodec implements FrameDecodec
{
	private final int frameLength;
	
	/**
	 * 固定长度解码器
	 * 
	 * @param frameLength 一个报文的固定长度
	 */
	public FixLengthDecodec(int frameLength)
	{
		this.frameLength = frameLength;
	}
	
	@Override
	public ByteBuf<?> decodec(ByteBuf<?> ioBuf) throws NotFitProtocolException, BufNotEnoughException, LessThanProtocolException
	{
		if (ioBuf.size() < frameLength)
		{
			throw new BufNotEnoughException(frameLength);
		}
		// 没有读取到足够的字节，并且本身是可以容纳一个报文长度的
		if (ioBuf.remainRead() < frameLength && ioBuf.size() >= frameLength)
		{
			return null;
		}
		else
		{
			ByteBuf<?> buf = DirectByteBufPool.getInstance().get(frameLength);
			buf.put(ioBuf, frameLength);
			ioBuf.addReadIndex(frameLength);
			return buf;
		}
	}
	
}
