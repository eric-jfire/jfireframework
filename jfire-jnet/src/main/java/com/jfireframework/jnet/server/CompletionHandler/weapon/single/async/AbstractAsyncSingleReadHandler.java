package com.jfireframework.jnet.server.CompletionHandler.weapon.single.async;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.DirectByteBuf;
import com.jfireframework.baseutil.disruptor.Disruptor;
import com.jfireframework.jnet.common.channel.impl.ServerChannel;
import com.jfireframework.jnet.server.CompletionHandler.weapon.single.AbstractSingleReadHandler;

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
        waeponTask.setChannelInfo(serverChannel);
        waeponTask.setData(intermediateResult);
        waeponTask.setIndex(0);
        disruptor.publish(this);
    }
    
    @Override
    public void asyncHandle()
    {
        try
        {
            Object intermediateResult = waeponTask.getData();
            for (int i = 0; i < handlers.length;)
            {
                intermediateResult = handlers[i].handle(intermediateResult, waeponTask);
                if (i == waeponTask.getIndex())
                {
                    i++;
                    waeponTask.setIndex(i);
                }
                else
                {
                    i = waeponTask.getIndex();
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
