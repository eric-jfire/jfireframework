package com.jfireframework.jnet2.client;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.DirectByteBuf;
import com.jfireframework.baseutil.resource.ResourceCloseAgent;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.jnet2.common.channel.ClientChannel;
import com.jfireframework.jnet2.common.decodec.DecodeResult;
import com.jfireframework.jnet2.common.decodec.FrameDecodec;
import com.jfireframework.jnet2.common.exception.EndOfStreamException;
import com.jfireframework.jnet2.common.exception.NotFitProtocolException;
import com.jfireframework.jnet2.common.handler.DataHandler;
import com.jfireframework.jnet2.common.result.InternalResult;
import com.jfireframework.jnet2.common.result.InternalResultImpl;
import com.jfireframework.jnet2.common.util.BytebufReleaseCallback;

public class ClientReadCompleter implements CompletionHandler<Integer, ClientChannel>
{
    private AsynchronousSocketChannel            socketChannel;
    private DataHandler[]                        handlers;
    private FrameDecodec                         frameDecodec;
    private final DirectByteBuf                  ioBuf          = DirectByteBuf.allocate(100);
    private final ClientChannel                  channelInfo;
    protected long                               readTimeout;
    protected long                               waitTimeout;
    private static final Logger                  logger         = ConsoleLogFactory.getLogger();
    private InternalResult                       internalResult = new InternalResultImpl();
    public int                                   total;
    private final ResourceCloseAgent<ByteBuf<?>> bufState       = new ResourceCloseAgent<ByteBuf<?>>(ioBuf, BytebufReleaseCallback.instance);
    
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
            catchThrowable(EndOfStreamException.instance);
            return;
        }
        ioBuf.addWriteIndex(result);
        while (true)
        {
            try
            {
                DecodeResult decodeResult = frameDecodec.decodec(ioBuf);
                switch (decodeResult.getType())
                {
                    case LESS_THAN_PROTOCOL:
                        readAndWait();
                        return;
                    case BUF_NOT_ENOUGH:
                        ioBuf.compact().ensureCapacity(decodeResult.getNeed());
                        continueRead();
                        return;
                    case NOT_FIT_PROTOCOL:
                        logger.debug("协议错误，关闭链接");
                        catchThrowable(NotFitProtocolException.instance);
                        return;
                    case NORMAL:
                        Object intermediateResult = decodeResult.getBuf();
                        internalResult.setChannelInfo(channelInfo);
                        internalResult.setIndex(0);
                        internalResult.setData(intermediateResult);
                        for (int i = 0; i < handlers.length;)
                        {
                            intermediateResult = handlers[i].handle(intermediateResult, internalResult);
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
                        channelInfo.signal(intermediateResult);
                        decodeResult.getBuf().release();
                        break;
                }
                if (ioBuf.remainRead() == 0)
                {
                    readAndWait();
                    return;
                }
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
        if (bufState.close())
        {
            Object tmp = e;
            for (DataHandler each : handlers)
            {
                tmp = each.catchException(tmp, internalResult);
            }
            channelInfo.signalAll(e);
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
        ByteBuffer ioBuffer = ioBuf.nioBuffer();
        ioBuffer.position(ioBuffer.limit()).limit(ioBuffer.capacity());
        return ioBuffer;
    }
    
}
