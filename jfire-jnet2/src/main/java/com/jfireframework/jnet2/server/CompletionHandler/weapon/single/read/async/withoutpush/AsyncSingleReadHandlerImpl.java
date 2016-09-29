package com.jfireframework.jnet2.server.CompletionHandler.weapon.single.read.async.withoutpush;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.jnet2.common.channel.impl.ServerChannel;
import com.jfireframework.jnet2.server.CompletionHandler.weapon.single.read.async.AbstractAsyncSingleReadHandler;
import com.jfireframework.jnet2.server.CompletionHandler.weapon.single.write.withoutpush.SyncSingleWriteHandlerImpl;

public class AsyncSingleReadHandlerImpl extends AbstractAsyncSingleReadHandler
{
    public AsyncSingleReadHandlerImpl(ServerChannel serverChannel, EventBus eventBus)
    {
        super(serverChannel, eventBus);
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
    
    @Override
    protected void doWrite(ByteBuf<?> buf)
    {
        writeHandler.write(buf);
    }
}
