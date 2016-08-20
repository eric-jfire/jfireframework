package com.jfireframework.jnet2.server.CompletionHandler.weapon.single;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.DirectByteBuf;
import com.jfireframework.baseutil.resource.ResourceCloseAgent;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.jnet2.common.channel.impl.ServerChannel;
import com.jfireframework.jnet2.common.decodec.FrameDecodec;
import com.jfireframework.jnet2.common.exception.EndOfStreamException;
import com.jfireframework.jnet2.common.handler.DataHandler;
import com.jfireframework.jnet2.common.result.InternalResult;
import com.jfireframework.jnet2.common.result.InternalResultImpl;
import com.jfireframework.jnet2.common.util.BytebufReleaseCallback;
import com.jfireframework.jnet2.server.CompletionHandler.WeaponWriteHandler;

public abstract class AbstractSingleReadHandler implements WeaponSingleReadHandler
{
    protected static final Logger            logger           = ConsoleLogFactory.getLogger();
    protected final FrameDecodec             frameDecodec;
    protected final DataHandler[]            handlers;
    protected final DirectByteBuf            ioBuf            = DirectByteBuf.allocate(100);
    protected final ServerChannel            serverChannel;
    // 读取超时时间
    protected final long                     readTimeout;
    protected final long                     waitTimeout;
    // 最后一次读取时间
    protected long                           lastReadTime;
    // 本次读取的截止时间
    protected long                           endReadTime;
    // 启动读取超时的计数
    protected boolean                        startCountdown   = false;
    protected final InternalResult           internalResult   = new InternalResultImpl();
    protected WeaponWriteHandler             writeHandler;
    protected ResourceCloseAgent<ByteBuf<?>> iobufReleseState = new ResourceCloseAgent<ByteBuf<?>>(ioBuf, BytebufReleaseCallback.instance);
    
    public AbstractSingleReadHandler(ServerChannel serverChannel)
    {
        this.serverChannel = serverChannel;
        frameDecodec = serverChannel.getFrameDecodec();
        handlers = serverChannel.getHandlers();
        readTimeout = serverChannel.getReadTimeout();
        waitTimeout = serverChannel.getWaitTimeout();
    }
    
    @Override
    public void completed(Integer read, ServerChannel channelInfo)
    {
        if (read == -1)
        {
            catchThrowable(EndOfStreamException.instance);
            return;
        }
        ioBuf.addWriteIndex(read);
        doRead();
    }
    
    @Override
    public void failed(Throwable exc, ServerChannel channelInfo)
    {
        catchThrowable(exc);
    }
    
    /**
     * 使用处理器处理异常，处理之后关闭当前的通道
     * 
     * @param exc
     */
    public void catchThrowable(Throwable exc)
    {
        if (serverChannel.closeChannel())
        {
            InternalResult task = new InternalResultImpl();
            task.setChannelInfo(serverChannel);
            task.setData(exc);
            task.setIndex(0);
            Object intermediateResult = exc;
            try
            {
                for (DataHandler each : handlers)
                {
                    intermediateResult = each.catchException(intermediateResult, task);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        iobufReleseState.close();
    }
    
    public void doRead()
    {
        try
        {
            frameAndHandle();
        }
        catch (Throwable e)
        {
            catchThrowable(e);
            return;
        }
    }
    
    protected abstract void frameAndHandle() throws Throwable;
    
    /**
     * 开始空闲读取等待，并且将倒数计时状态重置为false
     */
    public void readAndWait()
    {
        startCountdown = false;
        serverChannel.getSocketChannel().read(getWriteBuffer(), waitTimeout, TimeUnit.MILLISECONDS, serverChannel, this);
    }
    
    /**
     * 将iobuf的内容进行压缩，返回一个处于可写状态的ByteBuffer
     * 
     * @return
     */
    protected ByteBuffer getWriteBuffer()
    {
        ByteBuffer ioBuffer = ioBuf.nioBuffer();
        ioBuffer.position(ioBuffer.limit()).limit(ioBuffer.capacity());
        return ioBuffer;
    }
    
    /**
     * 在通道上继续读取未读取完整的数据
     */
    public void continueRead()
    {
        if (startCountdown == false)
        {
            lastReadTime = System.currentTimeMillis();
            endReadTime = lastReadTime + readTimeout;
            startCountdown = true;
        }
        serverChannel.getSocketChannel().read(getWriteBuffer(), getRemainTime(), TimeUnit.MILLISECONDS, serverChannel, this);
        lastReadTime = System.currentTimeMillis();
    }
    
    /**
     * 剩余的读取消息时间
     * 
     * @return
     */
    protected long getRemainTime()
    {
        long time = endReadTime - lastReadTime;
        return time;
    }
    
}
