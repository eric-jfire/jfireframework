package com.jfireframework.jnet.common.decodec;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.DirectByteBuf;
import com.jfireframework.jnet.common.exception.BufNotEnoughException;
import com.jfireframework.jnet.common.exception.LessThanProtocolException;
import com.jfireframework.jnet.common.exception.NotFitProtocolException;

/**
 * 报文长度整体frame解码器。其中需要解码的长度所代表的长度信息是保温整体的长度信息，也就是包含报文头和报文体一起的总长度
 * 
 * @author eric(eric@jfire.cn)
 * 
 */
public class TotalLengthFieldBasedFrameDecoder implements FrameDecodec
{
	// 代表长度字段开始读取的位置
	private final int	lengthFieldOffset;
	// 代表长度字段自身的长度。支持1,2,4.如果是1则使用unsignedbyte方式读取。如果是2则使用unsignedshort方式读取,4使用int方式读取。
	private final int	lengthFieldLength;
	// 将长度字段读取完毕，需要的偏移量,就是上面两个值相加
	private final int	lengthFieldEndOffset;
	// 需要忽略的字节数
	private final int	skipBytes;
	private final int	maxLegnth;
	
	/**
	 * 
	 * @param lengthFieldOffset 长度字段在报文中的偏移量
	 * @param lengthFieldLength 长度字段本身的长度
	 * @param skipBytes 解析后的报文需要跳过的位数
	 * @param maxLength
	 */
	public TotalLengthFieldBasedFrameDecoder(int lengthFieldOffset, int lengthFieldLength, int skipBytes, int maxLength)
	{
		this.lengthFieldLength = lengthFieldLength;
		this.lengthFieldOffset = lengthFieldOffset;
		this.maxLegnth = maxLength;
		lengthFieldEndOffset = lengthFieldOffset + lengthFieldLength;
		this.skipBytes = skipBytes;
	}
	
	@Override
	public ByteBuf<?> decodec(ByteBuf<?> ioBuffer) throws NotFitProtocolException, BufNotEnoughException, LessThanProtocolException
	{
		ioBuffer.maskRead();
		if (lengthFieldEndOffset > ioBuffer.remainRead())
		{
			return null;
		}
		//iobuffer中可能包含好几个报文，所以这里应该是增加的方式而不是直接设置的方式
		ioBuffer.addReadIndex(lengthFieldOffset);
		// 获取到整体报文的长度
		int length = 0;
		switch (lengthFieldLength)
		{
			case 1:
				length = ioBuffer.get() & 0xff;
				break;
			case 2:
				length = ioBuffer.readShort() & 0xff;
				break;
			case 4:
				length = ioBuffer.readInt();
				break;
		}
		// 得到整体长度后，开始从头读取这个长度的内容
		ioBuffer.resetRead();
		if (length >= maxLegnth)
		{
			throw NotFitProtocolException.instance;
		}
		if (length > ioBuffer.remainRead())
		{
			throw new BufNotEnoughException(length);
		}
		else
		{
			DirectByteBuf result = DirectByteBuf.allocate(length);
			result.put(ioBuffer, length);
			ioBuffer.addReadIndex(length);
			if (skipBytes != 0)
			{
				result.addReadIndex(skipBytes);
			}
			return result;
		}
	}
	
}
