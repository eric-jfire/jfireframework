package com.jfireframework.jnet.server.CompletionHandler.weapon.capacity.sync.withoutpush;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.DirectByteBuf;
import com.jfireframework.baseutil.concurrent.CpuCachePadingInt;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.jnet.common.channel.impl.ServerChannel;
import com.jfireframework.jnet.common.decodec.FrameDecodec;
import com.jfireframework.jnet.common.exception.BufNotEnoughException;
import com.jfireframework.jnet.common.exception.LessThanProtocolException;
import com.jfireframework.jnet.common.exception.NotFitProtocolException;
import com.jfireframework.jnet.common.handler.DataHandler;
import com.jfireframework.jnet.common.result.InternalResult;
import com.jfireframework.jnet.common.result.InternalResultImpl;
import com.jfireframework.jnet.common.util.ResourceState;
import com.jfireframework.jnet.server.CompletionHandler.weapon.capacity.sync.WeaponCapacityReadHandler;
import com.jfireframework.jnet.server.CompletionHandler.weapon.capacity.sync.WeaponCapacityWriteHandler;
import com.jfireframework.jnet.server.CompletionHandler.weapon.capacity.sync.write.withoutpush.WeaponCapacityWriteHandlerImpl;

public class CapacityReadHandlerImpl implements WeaponCapacityReadHandler
{
    
    private static final Logger              logger            = ConsoleLogFactory.getLogger();
    private final FrameDecodec               frameDecodec;
    private final DataHandler[]              handlers;
    private final DirectByteBuf              ioBuf             = DirectByteBuf.allocate(100);
    private final ServerChannel              serverChannel;
    private final static int                 WORK              = 1;
    private final static int                 IDLE              = 2;
    /**
     * 本线程仍然持有控制权
     */
    private final static int                 ON_CONTROL        = 1;
    
    /**
     * 本线程让渡出控制权
     */
    private final static int                 YIDLE             = 2;
    private final CpuCachePadingInt          readState         = new CpuCachePadingInt(WORK);
    // 读取超时时间
    private final long                       readTimeout;
    private final long                       waitTimeout;
    // 最后一次读取时间
    private long                             lastReadTime;
    // 本次读取的截止时间
    private long                             endReadTime;
    // 启动读取超时的计数
    private boolean                          startCountdown    = false;
    private final InternalResult             internalResult    = new InternalResultImpl();
    private final WeaponCapacityWriteHandler writeHandler;
    private final int                        capacity;
    private long                             wrap              = 0;
    // 下一个要填充到通道的序号
    private long                             cursor            = 0;
    private final ResourceState              openState         = new ResourceState();
    private final ResourceState              iobufReleaseState = new ResourceState();
    
    public CapacityReadHandlerImpl(ServerChannel serverChannel, int capacity)
    {
        this.serverChannel = serverChannel;
        this.capacity = capacity;
        wrap = capacity;
        writeHandler = new WeaponCapacityWriteHandlerImpl(serverChannel, capacity, this);
        frameDecodec = serverChannel.getFrameDecodec();
        handlers = serverChannel.getHandlers();
        readTimeout = serverChannel.getReadTimeout();
        waitTimeout = serverChannel.getWaitTimeout();
        internalResult.setChannelInfo(serverChannel);
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
        // 由于可能出现读取线程刚进入idle状态，就被写出线程抢占了work状态并且尝试读取数据，通道异常关闭后，第一次调用了fail方法。然后读取线程抢占回控制权，再次读取，立刻触发fail。导致fail方法有触发两次的风险。
        // 所以在这边使用一个资源状态来保护
        if (iobufReleaseState.close())
        {
            ioBuf.release();
        }
    }
    
    /**
     * 使用处理器处理异常，处理之后关闭当前的通道
     * 
     * @param exc
     */
    public void catchThrowable(Throwable exc)
    {
        if (openState.close())
        {
            try
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
            catch (Exception e)
            {
                logger.error("关闭通道异常", e);
            }
            serverChannel.closeChannel();
        }
    }
    
    public void doRead()
    {
        try
        {
            int result = frameAndHandle();
            if (result == ON_CONTROL)
            {
                readAndWait();
                return;
            }
            else
            {
                return;
            }
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
            logger.error("未预料的异常", e);
            catchThrowable(e);
            return;
        }
    }
    
    public int frameAndHandle() throws Throwable
    {
        while (true)
        {
            if (cursor < wrap)
            {
                Object intermediateResult = frameDecodec.decodec(ioBuf);
                internalResult.setData(intermediateResult);
                internalResult.setIndex(0);
                for (int i = 0; i < handlers.length;)
                {
                    intermediateResult = handlers[i].handle(intermediateResult, internalResult);
                    if (i == internalResult.getIndex())
                    {
                        i++;
                        internalResult.setIndex(i);
                    }
                    else
                    {
                        i = internalResult.getIndex();
                    }
                }
                if (intermediateResult instanceof ByteBuf<?>)
                {
                    writeHandler.write((ByteBuf<?>) intermediateResult, cursor);
                    cursor += 1;
                }
                if (ioBuf.remainRead() == 0)
                {
                    return ON_CONTROL;
                }
                else
                {
                    continue;
                }
            }
            else
            {
                wrap = writeHandler.cursor() + capacity;
                if (cursor < wrap)
                {
                    continue;
                }
                readState.set(IDLE);
                // 假设在这边失去控制权，然后写线程得到了控制权，然后注册了读取，通道异常关闭，触发一个fail操作。这边再次获得控制权后尝试读取。又会再次触发fail操作。导致iobuf被释放两次
                long tmp = writeHandler.cursor() + capacity;
                if (cursor >= tmp)
                {
                    return YIDLE;
                }
                else
                {
                    if (readState.compareAndSwap(IDLE, WORK))
                    {
                        wrap = writeHandler.cursor() + capacity;
                        continue;
                    }
                    else
                    {
                        return YIDLE;
                    }
                }
            }
        }
    }
    
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
    private ByteBuffer getWriteBuffer()
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
    private long getRemainTime()
    {
        long time = endReadTime - lastReadTime;
        return time;
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
    public long cursor()
    {
        return cursor;
    }
    
}
