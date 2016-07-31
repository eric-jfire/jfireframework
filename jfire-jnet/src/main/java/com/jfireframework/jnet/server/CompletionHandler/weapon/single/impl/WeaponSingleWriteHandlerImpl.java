package com.jfireframework.jnet.server.CompletionHandler.weapon.single.impl;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.concurrent.CpuCachePadingInt;
import com.jfireframework.baseutil.concurrent.MPSCLinkedQueue;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.jnet.common.channel.impl.ServerChannel;
import com.jfireframework.jnet.server.CompletionHandler.weapon.single.WeaponSingleReadHandler;
import com.jfireframework.jnet.server.CompletionHandler.weapon.single.WeaponSingleWriteHandler;

public class WeaponSingleWriteHandlerImpl implements WeaponSingleWriteHandler
{
    private final ServerChannel           serverChannel;
    private final WeaponSingleReadHandler readHandler;
    private Logger                        logger       = ConsoleLogFactory.getLogger();
    private static final int              IDLE         = 0;
    private static final int              WORK         = 1;
    private CpuCachePadingInt             state        = new CpuCachePadingInt(IDLE);
    private MPSCLinkedQueue<ByteBuf<?>>   waitForSends = new MPSCLinkedQueue<>();
    
    public WeaponSingleWriteHandlerImpl(ServerChannel serverChannel, WeaponSingleReadHandler readHandler)
    {
        this.serverChannel = serverChannel;
        this.readHandler = readHandler;
    }
    
    @Override
    public void completed(Integer result, ByteBuf<?> buf)
    {
        ByteBuffer buffer = buf.cachedNioBuffer();
        if (buffer.hasRemaining())
        {
            serverChannel.getSocketChannel().write(buffer, 10, TimeUnit.SECONDS, buf, this);
            return;
        }
        buf.release();
        if (waitForSends.isEmpty())
        {
            state.set(IDLE);
            readHandler.notifyRead();
        }
        else
        {
            buf = waitForSends.poll();
            serverChannel.getSocketChannel().write(buf.cachedNioBuffer(), 10, TimeUnit.SECONDS, buf, this);
        }
    }
    
    @Override
    public void failed(Throwable exc, ByteBuf<?> buf)
    {
        logger.error("error", exc);
        buf.release();
        readHandler.catchThrowable(exc);
    }
    
    @Override
    public void write(ByteBuf<?> buf)
    {
        if (state.value() == IDLE && state.compareAndSwap(IDLE, WORK))
        {
            serverChannel.getSocketChannel().write(buf.cachedNioBuffer(), 10, TimeUnit.SECONDS, buf, this);
        }
        else
        {
            waitForSends.add(buf);
            while (state.value() == IDLE && state.compareAndSwap(IDLE, WORK))
            {
                buf = waitForSends.poll();
                if (buf != null)
                {
                    serverChannel.getSocketChannel().write(buf.cachedNioBuffer(), 10, TimeUnit.SECONDS, buf, this);
                }
                state.set(IDLE);
                if (waitForSends.isEmpty())
                {
                    return;
                }
                else
                {
                    continue;
                }
            }
        }
    }
    
}
