package com.jfireframework.jnet.server.CompletionHandler.weapon.single.async.withoutpush;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.disruptor.Disruptor;
import com.jfireframework.jnet.common.channel.impl.ServerChannel;
import com.jfireframework.jnet.server.CompletionHandler.weapon.single.async.AbstractAsyncSingleReadHandler;
import com.jfireframework.jnet.server.CompletionHandler.weapon.single.write.withoutpush.SyncSingleWriteHandlerImpl;

public class AsyncSingleReadHandlerImpl extends AbstractAsyncSingleReadHandler
{
    public AsyncSingleReadHandlerImpl(ServerChannel serverChannel, Disruptor disruptor)
    {
        super(serverChannel, disruptor);
        writeHandler = new SyncSingleWriteHandlerImpl(serverChannel, this);
    }
    
    @Override
    public void notifyRead()
    {
        if (ioBuf.remainRead() > 0)
        {
            doRead();
        }
        else
        {
            readAndWait();
        }
    }
    
    protected void doWrite(ByteBuf<?> buf)
    {
        writeHandler.write(buf);
    }
}
