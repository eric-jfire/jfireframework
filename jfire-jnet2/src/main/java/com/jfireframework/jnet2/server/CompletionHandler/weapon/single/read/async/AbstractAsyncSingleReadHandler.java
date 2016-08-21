package com.jfireframework.jnet2.server.CompletionHandler.weapon.single.read.async;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.DirectByteBuf;
import com.jfireframework.baseutil.disruptor.Disruptor;
import com.jfireframework.jnet2.common.channel.impl.ServerChannel;
import com.jfireframework.jnet2.common.decodec.DecodeResult;
import com.jfireframework.jnet2.common.exception.NotFitProtocolException;
import com.jfireframework.jnet2.server.CompletionHandler.weapon.single.AbstractSingleReadHandler;

public abstract class AbstractAsyncSingleReadHandler extends AbstractSingleReadHandler implements AsyncReadHandler
{
    protected final Disruptor  disruptor;
    protected final ByteBuf<?> emptyBuf = DirectByteBuf.allocate(1);
    
    public AbstractAsyncSingleReadHandler(ServerChannel serverChannel, Disruptor disruptor)
    {
        super(serverChannel);
        this.disruptor = disruptor;
    }
    
    @Override
    protected void frameAndHandle() throws Throwable
    {
        DecodeResult decodeResult = frameDecodec.decodec(ioBuf);
        switch (decodeResult.getType())
        {
            case LESS_THAN_PROTOCOL:
                readAndWait();
                break;
            case BUF_NOT_ENOUGH:
                ioBuf.compact().ensureCapacity(decodeResult.getNeed());
                continueRead();
                break;
            case NOT_FIT_PROTOCOL:
                logger.debug("协议错误，关闭链接");
                catchThrowable(NotFitProtocolException.instance);
                break;
            case NORMAL:
                Object intermediateResult = decodeResult.getBuf();
                internalResult.setChannelInfo(serverChannel);
                internalResult.setData(intermediateResult);
                internalResult.setIndex(0);
                disruptor.publish(this);
                break;
        }
    }
    
    @Override
    public void asyncHandle()
    {
        try
        {
            Object intermediateResult = internalResult.getData();
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
            if (intermediateResult instanceof ByteBuf<?>)
            {
                doWrite((ByteBuf<?>) intermediateResult);
            }
            else
            {
                notifyRead();
            }
        }
        catch (Throwable e)
        {
            catchThrowable(e);
        }
    }
    
    protected abstract void doWrite(ByteBuf<?> buf);
}
