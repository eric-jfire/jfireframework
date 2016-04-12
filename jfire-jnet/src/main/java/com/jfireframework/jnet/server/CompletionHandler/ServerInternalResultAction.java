package com.jfireframework.jnet.server.CompletionHandler;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.disruptor.AbstractExclusiveEntryAction;
import com.jfireframework.baseutil.disruptor.Entry;
import com.jfireframework.jnet.common.handler.DataHandler;
import com.jfireframework.jnet.common.result.ServerInternalResult;

/**
 * 
 * entry的线程处理载体
 * 
 * @author 林斌
 * 
 */
public final class ServerInternalResultAction extends AbstractExclusiveEntryAction
{
	
	@Override
	public void doJob(Entry entry)
	{
		ServerInternalResult result = (ServerInternalResult) entry.getData();
		try
		{
			if (result.getChannelInfo().isOpen() == false)
			{
				logger.debug("通道{}已经关闭，不处理消息，直接退出", result.getChannelInfo().getAddress());
				return;
			}
			// 储存中间结果
			Object intermediateResult = result.getData();
			DataHandler[] handlers = result.getChannelInfo().getHandlers();
			for (int i = result.getIndex(); i < handlers.length;)
			{
				intermediateResult = handlers[i].handle(intermediateResult, result);
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
				if (result.getChannelInfo().isOpen())
				{
					result.getChannelInfo().write(result);
				}
			}
			else
			{
				
			}
		}
		catch (Exception e)
		{
			result.getChannelInfo().close(e);
		}
	}
	
}
