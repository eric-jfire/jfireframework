package com.jfireframework.jnet.server.CompletionHandler.weapon.single.write.withoutpush;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.jnet.common.channel.impl.ServerChannel;
import com.jfireframework.jnet.server.CompletionHandler.WeaponReadHandler;
import com.jfireframework.jnet.server.CompletionHandler.WeaponWriteHandler;

public class SyncSingleWriteHandlerImpl implements WeaponWriteHandler
{
    private final ServerChannel     serverChannel;
    private final WeaponReadHandler readHandler;
    private Logger                  logger = ConsoleLogFactory.getLogger();
    
    public SyncSingleWriteHandlerImpl(ServerChannel serverChannel, WeaponReadHandler readHandler)
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
        readHandler.notifyRead();
        buf.release();
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
    
    @Override
    public void push(ByteBuf<?> buf)
    {
        throw new UnsupportedOperationException();
    }
}
