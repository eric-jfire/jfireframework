package com.jfireframework.jnet.server.CompletionHandler;

import java.nio.channels.CompletionHandler;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.jnet.common.decodec.FrameDecodec;
import com.jfireframework.jnet.common.exception.BufNotEnoughException;
import com.jfireframework.jnet.common.exception.EndOfStreamException;
import com.jfireframework.jnet.common.exception.LessThanProtocolException;
import com.jfireframework.jnet.common.exception.NotFitProtocolException;
import com.jfireframework.jnet.common.handler.DataHandler;
import com.jfireframework.jnet.common.result.ServerInternalResult;
import com.jfireframework.jnet.server.server.ServerChannelInfo;

public class ChannelReadHandler implements CompletionHandler<Integer, ServerChannelInfo>
{
	private Logger			logger			= ConsoleLogFactory.getLogger();
	private FrameDecodec	frameDecodec;
	private static int		DEAL_NEXT		= 1;
	private static int		CONTINUE_READ	= 2;
	
	public ChannelReadHandler(FrameDecodec frameDecodec)
	{
		this.frameDecodec = frameDecodec;
	}
	
	@Override
	public void completed(Integer read, ServerChannelInfo channelInfo)
	{
		if (read == -1)
		{
			channelInfo.close(new EndOfStreamException());
			return;
		}
		channelInfo.ioBuf().addWriteIndex(read);
		while (true)
		{
			try
			{
				if (frameAndHandle(channelInfo) == CONTINUE_READ)
				{
					channelInfo.startReadWait();
					return;
				}
				else
				{
					continue;
				}
			}
			catch (NotFitProtocolException e)
			{
				logger.debug("协议错误，关闭链接");
				channelInfo.close(e);
				return;
			}
			catch (LessThanProtocolException e)
			{
				channelInfo.startReadWait();
				return;
			}
			catch (BufNotEnoughException e)
			{
				ByteBuf<?> ioBuf = channelInfo.ioBuf();
				ioBuf.ensureCapacity(e.getNeedSize());
				channelInfo.continueRead();
				return;
			}
			catch (Throwable e)
			{
				channelInfo.close(e);
				return;
			}
		}
	}
	
	private int frameAndHandle(ServerChannelInfo channelInfo) throws Exception
	{
		
		Object intermediateResult = frameDecodec.decodec(channelInfo.ioBuf());
		if (intermediateResult == null)
		{
			return CONTINUE_READ;
		}
		ServerInternalResult result = new ServerInternalResult(intermediateResult, channelInfo, 0);
		channelInfo.addWriteResult(result);
		DataHandler[] handlers = channelInfo.getHandlers();
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
			channelInfo.write(result);
		}
		if (channelInfo.ioBuf().remainRead() == 0)
		{
			return CONTINUE_READ;
		}
		return DEAL_NEXT;
		
	}
	
	@Override
	public void failed(Throwable exc, ServerChannelInfo channelInfo)
	{
		channelInfo.close(exc);
	}
	
}
