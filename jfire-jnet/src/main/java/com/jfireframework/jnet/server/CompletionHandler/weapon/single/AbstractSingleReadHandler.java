package com.jfireframework.jnet.server.CompletionHandler.weapon.single;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import com.jfireframework.baseutil.collection.buffer.DirectByteBuf;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.jnet.common.channel.impl.ServerChannel;
import com.jfireframework.jnet.common.decodec.FrameDecodec;
import com.jfireframework.jnet.common.exception.BufNotEnoughException;
import com.jfireframework.jnet.common.exception.LessThanProtocolException;
import com.jfireframework.jnet.common.exception.NotFitProtocolException;
import com.jfireframework.jnet.common.handler.DataHandler;
import com.jfireframework.jnet.common.result.WeaponTask;
import com.jfireframework.jnet.server.CompletionHandler.weapon.WeaponReadHandler;
import com.jfireframework.jnet.server.CompletionHandler.weapon.WeaponWriteHandler;

public abstract class AbstractSingleReadHandler implements WeaponReadHandler
{
    protected static final Logger logger         = ConsoleLogFactory.getLogger();
    protected final FrameDecodec  frameDecodec;
    protected final DataHandler[] handlers;
    protected final DirectByteBuf ioBuf          = DirectByteBuf.allocate(100);
    protected final ServerChannel serverChannel;
    // 读取超时时间
    protected final long          readTimeout;
    protected final long          waitTimeout;
    // 最后一次读取时间
    protected long                lastReadTime;
    // 本次读取的截止时间
    protected long                endReadTime;
    // 启动读取超时的计数
    protected boolean             startCountdown = false;
    protected final WeaponTask    waeponTask     = new WeaponTask();
    protected WeaponWriteHandler  writeHandler;
    
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
            channelInfo.closeChannel();
            return;
        }
        ioBuf.addWriteIndex(read);
        doRead();
    }
    
    @Override
    public void failed(Throwable exc, ServerChannel channelInfo)
    {
        catchThrowable(exc);
        ioBuf.release();
    }
    
    /**
     * 使用处理器处理异常，处理之后关闭当前的通道
     * 
     * @param exc
     */
    public void catchThrowable(Throwable exc)
    {
        try
        {
            WeaponTask task = new WeaponTask();
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
        catch (Exception e)
        {
            logger.error("关闭通道异常", e);
        }
        serverChannel.closeChannel();
        /**
         * 这个方法里不能去释放iobuf。因为这个方法有可能是异步处理的时候被调用，这样通道还没有关闭的情况下就先释放了iobuf，
         * 然后关闭通道又释放一次就会造成错误
         * 或者是该方法中被释放，其他地方回收了又再次使用，然后通过中关闭的时候释放掉，就错误的释放了别的地方的ioBuf。
         * 所以这个方法中是不可以释放iobuf的，
         * 一定是要在ReadCompletionHandler的complete或者fail方法中完成对iobuf的释放
         */
    }
    
    public void doRead()
    {
        try
        {
            frameAndHandle();
        }
        catch (LessThanProtocolException e)
        {
            readAndWait();
            return;
        }
        catch (BufNotEnoughException e)
        {
            ioBuf.compact().ensureCapacity(e.getNeedSize());
            continueRead();
            return;
        }
        catch (NotFitProtocolException e)
        {
            logger.debug("协议错误，关闭链接");
            catchThrowable(e);
            return;
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
        ioBuf.compact();
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
