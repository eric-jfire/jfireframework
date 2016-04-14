package com.jfireframework.jnet.server.CompletionHandler;

import java.io.IOException;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.DirectByteBuf;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.jnet.common.channel.ServerChannelInfo;
import com.jfireframework.jnet.common.decodec.FrameDecodec;
import com.jfireframework.jnet.common.exception.BufNotEnoughException;
import com.jfireframework.jnet.common.exception.EndOfStreamException;
import com.jfireframework.jnet.common.exception.LessThanProtocolException;
import com.jfireframework.jnet.common.exception.NotFitProtocolException;
import com.jfireframework.jnet.common.handler.DataHandler;
import com.jfireframework.jnet.common.result.ServerInternalResult;

public class ChannelReadHandler implements CompletionHandler<Integer, ServerChannelInfo>
{
	private static final Logger	logger			= ConsoleLogFactory.getLogger();
	private FrameDecodec		frameDecodec;
	private DataHandler[]		handlers;
	private final DirectByteBuf	ioBuf			= DirectByteBuf.allocate(100);
	private ServerChannelInfo	channelInfo;
	public final static int		CONTINUE_READ	= 1;
	// 暂时不监听监听当前通道上的数据
	public final static int		FREE_OF_READ	= 2;
	private volatile int		readState		= 0;
	public final static long	readStateOff;
	public final static int		IN_READ			= 1;
	public final static int		OUT_OF_READ		= 2;
	private volatile long		cursor;
	private long				wrapPoint		= 0;
	private ChannelWriteHandler	writeHandler;
	static
	{
		readStateOff = ReflectUtil.getFieldOffset("readState", ServerChannelInfo.class);
	}
	
	@Override
	public void completed(Integer read, ServerChannelInfo channelInfo)
	{
		if (read == -1)
		{
			channelInfo.closeChannel();
			return;
		}
		ioBuf.addWriteIndex(read);
		doRead();
	}
	
	@Override
	public void failed(Throwable exc, ServerChannelInfo channelInfo)
	{
		catchThrowable(exc);
		channelInfo.closeChannel();
	}
	
	public void catchThrowable(Throwable exc)
	{
		try
		{
			ServerInternalResult result = new ServerInternalResult(-1, exc, channelInfo, 0);
			Object intermediateResult = exc;
			try
			{
				for (DataHandler each : handlers)
				{
					intermediateResult = each.catchException(intermediateResult, result);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		catch (Exception e)
		{
			logger.error("关闭通道异常", e);
		}
		channelInfo.closeChannel();
		ioBuf.release();
	}
	
	public void doRead()
	{
		while (true)
		{
			try
			{
				int result = frameAndHandle();
				if (result == CONTINUE_READ)
				{
					startReadWait();
					return;
				}
				else if (result == FREE_OF_READ)
				{
					readState = OUT_OF_READ;
					return;
				}
			}
			catch (NotFitProtocolException e)
			{
				logger.debug("协议错误，关闭链接");
				close(e);
				return;
			}
			catch (LessThanProtocolException e)
			{
				startReadWait();
				return;
			}
			catch (BufNotEnoughException e)
			{
				ioBuf.compact().ensureCapacity(e.getNeedSize());
				continueRead();
				return;
			}
			catch (Throwable e)
			{
				close(e);
				return;
			}
		}
	}
	
	public int frameAndHandle() throws Exception
	{
		while (true)
		{
			if (cursor >= wrapPoint)
			{
				for (int i = 0; i < 2; i++)
				{
					wrapPoint = writeHandler.cursor() + channelInfo.getEntryArraySize();
					if (cursor < wrapPoint)
					{
						break;
					}
				}
				if (cursor >= wrapPoint)
				{
					readState = OUT_OF_READ;
					return FREE_OF_READ;
				}
			}
			Object intermediateResult = frameDecodec.decodec(ioBuf);
			if (intermediateResult == null)
			{
				return CONTINUE_READ;
			}
			ServerInternalResult result = (ServerInternalResult) channelInfo.getEntry(cursor);
			if (result == null)
			{
				result = new ServerInternalResult(cursor, intermediateResult, channelInfo, 0);
				channelInfo.putEntry(result, cursor);
			}
			else
			{
				result.init(cursor, intermediateResult, channelInfo, 0);
			}
			cursor += 1;
			for (int i = 0; i < handlers.length;)
			{
				intermediateResult = handlers[i].handle(intermediateResult, result);
				if (intermediateResult == DataHandler.skipToWorkRing)
				{
					break;
				}
				if (i == result.getIndex())
				{
					i++;
					result.setIndex(i);
				}
				else
				{
					i = result.getIndex();
				}
			}
			if (intermediateResult instanceof ByteBuf<?>)
			{
				result.setData(intermediateResult);
				result.flowDone();
				write(result);
			}
			if (ioBuf.remainRead() == 0)
			{
				return CONTINUE_READ;
			}
		}
		
	}
	
	public void write(ServerInternalResult result)
	{
		if (result.tryWrite())
		{
			channelInfo.socketChannel().write(((ByteBuf<?>) result.getData()).nioBuffer(), 10, TimeUnit.SECONDS, result, writeHandler);
		}
	}
}
