package com.jfireframework.jnet.server.CompletionHandler.single.impl;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.jnet.common.channel.impl.ServerChannel;
import com.jfireframework.jnet.server.CompletionHandler.single.WeaponSingleReadHandler;
import com.jfireframework.jnet.server.CompletionHandler.single.WeaponSingleWriteHandler;

public class WeaponSingleWriteHandlerImpl implements WeaponSingleWriteHandler
{
    private final ServerChannel           serverChannel;
    private final WeaponSingleReadHandler readHandler;
    private Logger                        logger = ConsoleLogFactory.getLogger();
    
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
        readHandler.notifyRead();
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
        serverChannel.getSocketChannel().write(buf.cachedNioBuffer(), 10, TimeUnit.SECONDS, buf, this);
    }
    
}
