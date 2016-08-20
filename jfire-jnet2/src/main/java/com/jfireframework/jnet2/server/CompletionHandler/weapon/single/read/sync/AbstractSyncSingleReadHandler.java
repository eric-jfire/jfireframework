package com.jfireframework.jnet2.server.CompletionHandler.weapon.single.read.sync;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.jnet2.common.channel.impl.ServerChannel;
import com.jfireframework.jnet2.common.decodec.DecodeResult;
import com.jfireframework.jnet2.common.exception.NotFitProtocolException;
import com.jfireframework.jnet2.server.CompletionHandler.weapon.single.AbstractSingleReadHandler;

public abstract class AbstractSyncSingleReadHandler extends AbstractSingleReadHandler
{
    
    public AbstractSyncSingleReadHandler(ServerChannel serverChannel)
    {
        super(serverChannel);
    }
    
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
                break;
        }
    }
    
    protected abstract void doWrite(ByteBuf<?> buf);
}
