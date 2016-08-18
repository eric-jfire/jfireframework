package com.jfireframework.jnet2.server.CompletionHandler.weapon.single.read.async;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.DirectByteBuf;
import com.jfireframework.baseutil.disruptor.Disruptor;
import com.jfireframework.jnet2.common.channel.impl.ServerChannel;
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
        Object intermediateResult = frameDecodec.decodec(ioBuf);
        internalResult.setChannelInfo(serverChannel);
        internalResult.setData(intermediateResult);
        internalResult.setIndex(0);
        disruptor.publish(this);
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
                doWrite(emptyBuf);
            }
        }
        catch (Throwable e)
        {
            catchThrowable(e);
        }
    }
    
    protected abstract void doWrite(ByteBuf<?> buf);
}
