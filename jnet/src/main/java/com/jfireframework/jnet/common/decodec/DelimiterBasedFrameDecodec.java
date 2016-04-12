package com.jfireframework.jnet.common.decodec;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.DirectByteBuf;
import com.jfireframework.baseutil.collection.buffer.DirectByteBufPool;
import com.jfireframework.jnet.common.exception.BufNotEnoughException;
import com.jfireframework.jnet.common.exception.LessThanProtocolException;
import com.jfireframework.jnet.common.exception.NotFitProtocolException;

/**
 * 特定结束符整包解码器
 * 
 * @author 林斌
 * 
 */
public class DelimiterBasedFrameDecodec implements FrameDecodec
{
	private byte[]	delimiter;
	private int		maxLength;
	
	/**
	 * 
	 * @param delimiter 解码使用的特定字节数组
	 * @param maxLength 读取的码流最大长度。超过这个长度还未发现结尾分割字节数组，就会抛出异常
	 */
	public DelimiterBasedFrameDecodec(byte[] delimiter, int maxLength)
	{
		this.maxLength = maxLength;
		this.delimiter = delimiter;
	}
	
	@Override
	public ByteBuf<?> decodec(ByteBuf<?> ioBuffer) throws NotFitProtocolException, BufNotEnoughException, LessThanProtocolException
	{
		if (ioBuffer.remainRead() > maxLength)
		{
			throw NotFitProtocolException.instance;
		}
		ioBuffer.maskRead();
		int index = ioBuffer.indexOf(delimiter);
		if (index == -1)
		{
			return null;
		}
		else
		{
			int contentLength = index - ioBuffer.readIndex();
			DirectByteBuf buf = DirectByteBufPool.getInstance().get(contentLength);
			buf.put(ioBuffer, contentLength);
			ioBuffer.readIndex(index + delimiter.length);
			return buf;
		}
	}
	
}
