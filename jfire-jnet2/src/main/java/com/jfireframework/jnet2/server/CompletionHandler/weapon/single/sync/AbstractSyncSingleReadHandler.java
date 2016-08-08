package com.jfireframework.jnet2.server.CompletionHandler.weapon.single.sync;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.jnet2.common.channel.impl.ServerChannel;
import com.jfireframework.jnet2.server.CompletionHandler.weapon.single.AbstractSingleReadHandler;

public abstract class AbstractSyncSingleReadHandler extends AbstractSingleReadHandler
{
    
    public AbstractSyncSingleReadHandler(ServerChannel serverChannel)
    {
        super(serverChannel);
    }
    
    protected void frameAndHandle() throws Throwable
    {
        while (true)
        {
            Object intermediateResult = frameDecodec.decodec(ioBuf);
            internalResult.setChannelInfo(serverChannel);
            internalResult.setData(intermediateResult);
            internalResult.setIndex(0);
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
                break;
            }
            else
            {
                if (ioBuf.remainRead() > 0)
                {
                    continue;
                }
                else
                {
                    readAndWait();
                    break;
                }
            }
        }
    }
    
    protected abstract void doWrite(ByteBuf<?> buf);
}
