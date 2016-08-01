package com.jfireframework.jnet.server.CompletionHandler.weapon.single.sync.withoutpush;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.jnet.common.channel.impl.ServerChannel;
import com.jfireframework.jnet.server.CompletionHandler.weapon.single.sync.AbstractReadHandler;

public class SyncReadHandlerImpl extends AbstractReadHandler
{
    
    public SyncReadHandlerImpl(ServerChannel serverChannel)
    {
        super(serverChannel);
        writeHandler = new SyncWriteHandlerImpl(serverChannel, this);
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
