package com.jfireframework.jnet.client;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;
import com.jfireframework.baseutil.collection.buffer.DirectByteBuf;
import com.jfireframework.jnet.common.channel.ClientChannelInfo;
import com.jfireframework.jnet.common.decodec.FrameDecodec;
import com.jfireframework.jnet.common.exception.BufNotEnoughException;
import com.jfireframework.jnet.common.exception.EndOfStreamException;
import com.jfireframework.jnet.common.exception.LessThanProtocolException;
import com.jfireframework.jnet.common.exception.NotFitProtocolException;
import com.jfireframework.jnet.common.handler.DataHandler;
import com.jfireframework.jnet.common.result.ClientInternalResult;

public class ClientReadCompleter implements CompletionHandler<Integer, ClientChannelInfo>
{
	private AsynchronousSocketChannel	socketChannel;
	private DataHandler[]				handlers;
	private FrameDecodec				frameDecodec;
	private final boolean				futureClient;
	private volatile long				cursor	= 0;
	private final DirectByteBuf			ioBuf	= DirectByteBuf.allocate(100);
	private final ClientChannelInfo		channelInfo;
	protected long						readTimeout;
	protected long						waitTimeout;
	
	public ClientReadCompleter(AioClient aioClient, ClientChannelInfo channelInfo)
	{
		if (aioClient instanceof FutureClient)
		{
			futureClient = true;
		}
		else
		{
			futureClient = false;
		}
		this.channelInfo = channelInfo;
		readTimeout = channelInfo.getReadTimeout();
		waitTimeout = channelInfo.getWaitTimeout();
		frameDecodec = channelInfo.getFrameDecodec();
		handlers = channelInfo.getHandlers();
		socketChannel = channelInfo.socketChannel();
	}
	
	public long cursor()
	{
		return cursor;
	}
	
	@Override
	public void completed(Integer result, ClientChannelInfo channelInfo)
	{
		if (result == -1)
		{
			catchThrowable(new EndOfStreamException());
			return;
		}
		ioBuf.addWriteIndex(result);
		Object decodeResult = null;
		do
		{
			try
			{
				decodeResult = frameDecodec.decodec(ioBuf);
				if (decodeResult != null)
				{
					ClientInternalResult internalResult = new ClientInternalResult(decodeResult, channelInfo, 0);
					for (int i = 0; i < handlers.length;)
					{
						decodeResult = handlers[i].handle(decodeResult, internalResult);
						if (i == internalResult.getIndex())
						{
							i++;
							internalResult.setIndex(i);
						}
						else
						{
							i = internalResult.getIndex();
						}
					}
					if (futureClient)
					{
						channelInfo.signal(decodeResult, cursor);
						cursor += 1;
					}
				}
				if (ioBuf.remainRead() == 0)
				{
					readAndWait();
					return;
				}
			}
			catch (NotFitProtocolException e)
			{
				catchThrowable(e);
				return;
			}
			catch (BufNotEnoughException e)
			{
				ioBuf.compact();
				ioBuf.ensureCapacity(e.getNeedSize());
				continueRead();
				return;
			}
			catch (LessThanProtocolException e)
			{
				readAndWait();
				return;
			}
			catch (Exception e)
			{
				catchThrowable(e);
				return;
			}
		} while (decodeResult != null);
		readAndWait();
	}
	
	@Override
	public void failed(Throwable exc, ClientChannelInfo channelInfo)
	{
		catchThrowable(exc);
	}
	
	private void catchThrowable(Throwable e)
	{
		channelInfo.closeChannel();
		ClientInternalResult result = new ClientInternalResult(e, null, 0);
		Object tmp = e;
		for (DataHandler each : handlers)
		{
			tmp = each.catchException(tmp, result);
		}
		ioBuf.release();
		if (futureClient)
		{
			channelInfo.signalAll(e, cursor);
		}
	}
	
	public void continueRead()
	{
		socketChannel.read(getReadBuffer(), readTimeout, TimeUnit.MILLISECONDS, channelInfo, this);
	}
	
	public void readAndWait()
	{
		socketChannel.read(getReadBuffer(), waitTimeout, TimeUnit.MILLISECONDS, channelInfo, this);
	}
	
	private ByteBuffer getReadBuffer()
	{
		ioBuf.compact();
		ByteBuffer ioBuffer = ioBuf.nioBuffer();
		ioBuffer.position(ioBuffer.limit()).limit(ioBuffer.capacity());
		return ioBuffer;
	}
	
}
