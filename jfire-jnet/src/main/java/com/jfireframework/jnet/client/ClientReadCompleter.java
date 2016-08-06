package com.jfireframework.jnet.client;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;
import com.jfireframework.baseutil.collection.buffer.DirectByteBuf;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.jnet.common.channel.ClientChannel;
import com.jfireframework.jnet.common.decodec.FrameDecodec;
import com.jfireframework.jnet.common.exception.BufNotEnoughException;
import com.jfireframework.jnet.common.exception.EndOfStreamException;
import com.jfireframework.jnet.common.exception.LessThanProtocolException;
import com.jfireframework.jnet.common.exception.NotFitProtocolException;
import com.jfireframework.jnet.common.handler.DataHandler;
import com.jfireframework.jnet.common.result.InternalResult;
import com.jfireframework.jnet.common.result.InternalResultImpl;

public class ClientReadCompleter implements CompletionHandler<Integer, ClientChannel>
{
    private AsynchronousSocketChannel socketChannel;
    private DataHandler[]             handlers;
    private FrameDecodec              frameDecodec;
    private final DirectByteBuf       ioBuf          = DirectByteBuf.allocate(100);
    private final ClientChannel       channelInfo;
    protected long                    readTimeout;
    protected long                    waitTimeout;
    private static final Logger       logger         = ConsoleLogFactory.getLogger();
    private InternalResult            internalResult = new InternalResultImpl();
    public int                        total;
    
    public ClientReadCompleter(AioClient aioClient, ClientChannel channelInfo)
    {
        this.channelInfo = channelInfo;
        readTimeout = channelInfo.getReadTimeout();
        waitTimeout = channelInfo.getWaitTimeout();
        frameDecodec = channelInfo.getFrameDecodec();
        handlers = channelInfo.getHandlers();
        socketChannel = channelInfo.getSocketChannel();
    }
    
    @Override
    public void completed(Integer result, ClientChannel channelInfo)
    {
        if (result == -1)
        {
            catchThrowable(new EndOfStreamException());
            return;
        }
        ioBuf.addWriteIndex(result);
        Object decodeResult = null;
        while (true)
        {
            try
            {
                decodeResult = frameDecodec.decodec(ioBuf);
                if (decodeResult != null)
                {
                    internalResult.setChannelInfo(channelInfo);
                    internalResult.setIndex(0);
                    internalResult.setData(decodeResult);
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
                    // logger.trace("客户端处理完毕响应{}", cursor);
                    channelInfo.signal(decodeResult);
                }
                if (ioBuf.remainRead() == 0)
                {
                    readAndWait();
                    return;
                }
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
            catch (NotFitProtocolException e)
            {
                catchThrowable(e);
                return;
            }
            catch (Throwable e)
            {
                logger.error("未知异常", e);
                catchThrowable(e);
                return;
            }
        }
    }
    
    @Override
    public void failed(Throwable exc, ClientChannel channelInfo)
    {
        catchThrowable(exc);
    }
    
    private void catchThrowable(Throwable e)
    {
        channelInfo.closeChannel();
        Object tmp = e;
        for (DataHandler each : handlers)
        {
            tmp = each.catchException(tmp, internalResult);
        }
        ioBuf.release();
        channelInfo.signalAll(e);
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
        ByteBuffer ioBuffer = ioBuf.nioBuffer();
        ioBuffer.position(ioBuffer.limit()).limit(ioBuffer.capacity());
        return ioBuffer;
    }
    
}
