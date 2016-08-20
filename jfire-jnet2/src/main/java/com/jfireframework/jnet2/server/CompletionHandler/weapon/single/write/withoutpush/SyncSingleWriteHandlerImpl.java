package com.jfireframework.jnet2.server.CompletionHandler.weapon.single.write.withoutpush;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.jnet2.common.channel.impl.ServerChannel;
import com.jfireframework.jnet2.server.CompletionHandler.WeaponWriteHandler;
import com.jfireframework.jnet2.server.CompletionHandler.weapon.single.WeaponSingleReadHandler;

public class SyncSingleWriteHandlerImpl implements WeaponWriteHandler
{
    private final ServerChannel           serverChannel;
    private final WeaponSingleReadHandler readHandler;
    private final static Logger           logger = ConsoleLogFactory.getLogger();
    
    public SyncSingleWriteHandlerImpl(ServerChannel serverChannel, WeaponSingleReadHandler readHandler)
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
    
    @Override
    public void push(ByteBuf<?> buf)
    {
        throw new UnsupportedOperationException();
    }
}
