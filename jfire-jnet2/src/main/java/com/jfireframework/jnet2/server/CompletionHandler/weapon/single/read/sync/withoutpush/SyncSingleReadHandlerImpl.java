package com.jfireframework.jnet2.server.CompletionHandler.weapon.single.read.sync.withoutpush;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.jnet2.common.channel.impl.ServerChannel;
import com.jfireframework.jnet2.server.CompletionHandler.weapon.single.read.sync.AbstractSyncSingleReadHandler;
import com.jfireframework.jnet2.server.CompletionHandler.weapon.single.write.withoutpush.SyncSingleWriteHandlerImpl;

public class SyncSingleReadHandlerImpl extends AbstractSyncSingleReadHandler
{
    
    public SyncSingleReadHandlerImpl(ServerChannel serverChannel)
    {
        super(serverChannel);
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
