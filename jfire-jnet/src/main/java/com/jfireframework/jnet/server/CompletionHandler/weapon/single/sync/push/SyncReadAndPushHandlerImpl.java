package com.jfireframework.jnet.server.CompletionHandler.weapon.single.sync.push;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.concurrent.CpuCachePadingInt;
import com.jfireframework.jnet.common.channel.impl.ServerChannel;
import com.jfireframework.jnet.server.CompletionHandler.weapon.single.sync.AbstractReadHandler;

public class SyncReadAndPushHandlerImpl extends AbstractReadHandler
{
    private final static int        IDLE      = 0;
    private final static int        WORK      = 1;
    private final CpuCachePadingInt readState = new CpuCachePadingInt(0);
    
    public SyncReadAndPushHandlerImpl(ServerChannel serverChannel)
    {
        super(serverChannel);
        writeHandler = new SyncWriteAndPushHandlerImpl(serverChannel, this);
    }
    
    @Override
    public void notifyRead()
    {
        if (readState.value() == IDLE && readState.compareAndSwap(IDLE, WORK))
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
    }
    
    @Override
    protected void doWrite(ByteBuf<?> buf)
    {
        readState.set(IDLE);
        writeHandler.write(buf);
    }
    
}
