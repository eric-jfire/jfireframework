package com.jfireframework.jnet.server.CompletionHandler.weapon.single.sync.push;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.concurrent.CpuCachePadingInt;
import com.jfireframework.jnet.common.channel.impl.ServerChannel;
import com.jfireframework.jnet.server.CompletionHandler.weapon.single.sync.AbstractSyncSingleReadHandler;
import com.jfireframework.jnet.server.CompletionHandler.weapon.single.write.push.SyncSingleWriteAndPushHandlerImpl;

public class SyncSingleReadAndPushHandlerImpl extends AbstractSyncSingleReadHandler
{
    private final static int        IDLE      = 0;
    private final static int        PENDING   = 1;
    private final static int        WORK      = 2;
    private final CpuCachePadingInt readState = new CpuCachePadingInt(0);
    
    public SyncSingleReadAndPushHandlerImpl(ServerChannel serverChannel)
    {
        super(serverChannel);
        writeHandler = new SyncSingleWriteAndPushHandlerImpl(serverChannel, this);
    }
    
    @Override
    public void notifyRead()
    {
        int state = readState.value();
        if (state == WORK)
        {
            return;
        }
        if (state == PENDING)
        {
            do
            {
                state = readState.value();
            } while (state == PENDING);
        }
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
        /**
         * 这边有中间状态的原因是如果在写出之前就设置为idle,那么假设尚未执行到下面的write语句，有用户主动push消息,
         * 在写出处理器中执行了notifyread.会导致另外的线程中再次执行doread。有可能造成后来读取的消息反而先处理的错误情况
         * 所以在这个地方加入了中间状态，保证了不会出现这种问题
         */
        readState.set(PENDING);
        writeHandler.write(buf);
        readState.set(IDLE);
    }
    
}
