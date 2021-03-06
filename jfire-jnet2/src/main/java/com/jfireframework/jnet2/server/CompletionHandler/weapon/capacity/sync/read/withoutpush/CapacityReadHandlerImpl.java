package com.jfireframework.jnet2.server.CompletionHandler.weapon.capacity.sync.read.withoutpush;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.DirectByteBuf;
import com.jfireframework.baseutil.concurrent.CpuCachePadingInt;
import com.jfireframework.baseutil.resource.ResourceCloseAgent;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.jnet2.common.channel.impl.ServerChannel;
import com.jfireframework.jnet2.common.decodec.DecodeResult;
import com.jfireframework.jnet2.common.decodec.FrameDecodec;
import com.jfireframework.jnet2.common.exception.EndOfStreamException;
import com.jfireframework.jnet2.common.exception.NotFitProtocolException;
import com.jfireframework.jnet2.common.handler.DataHandler;
import com.jfireframework.jnet2.common.result.InternalResult;
import com.jfireframework.jnet2.common.result.InternalResultImpl;
import com.jfireframework.jnet2.common.util.BytebufReleaseCallback;
import com.jfireframework.jnet2.server.CompletionHandler.weapon.capacity.sync.CapacityReadHandler;
import com.jfireframework.jnet2.server.CompletionHandler.weapon.capacity.sync.WeaponCapacityWriteHandler;
import com.jfireframework.jnet2.server.CompletionHandler.weapon.capacity.sync.write.withoutpush.WeaponCapacityWriteHandlerImpl;

public class CapacityReadHandlerImpl implements CapacityReadHandler
{
    
    private static final Logger                  logger            = ConsoleLogFactory.getLogger();
    private final FrameDecodec                   frameDecodec;
    private final DataHandler[]                  handlers;
    private final DirectByteBuf                  ioBuf             = DirectByteBuf.allocate(100);
    private final ServerChannel                  serverChannel;
    private final static int                     WORK              = 1;
    private final static int                     IDLE              = 2;
    private final CpuCachePadingInt              readState         = new CpuCachePadingInt(WORK);
    // 读取超时时间
    private final long                           readTimeout;
    private final long                           waitTimeout;
    // 最后一次读取时间
    private long                                 lastReadTime;
    // 本次读取的截止时间
    private long                                 endReadTime;
    // 启动读取超时的计数
    private boolean                              startCountdown    = false;
    private final InternalResult                 internalResult    = new InternalResultImpl();
    private final WeaponCapacityWriteHandler     writeHandler;
    private final int                            capacity;
    private long                                 wrap              = 0;
    // 下一个要填充到通道的序号
    private long                                 cursor            = 0;
    private final ResourceCloseAgent<ByteBuf<?>> iobufReleaseState = new ResourceCloseAgent<ByteBuf<?>>(ioBuf, BytebufReleaseCallback.instance);
    private final ByteBuffer                     activeFlag        = ByteBuffer.allocateDirect(0);
    
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
            catchThrowable(EndOfStreamException.instance);
            iobufReleaseState.close();
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
        iobufReleaseState.close();
    }
    
    /**
     * 关闭通道，并且执行异常处理。关闭通道的机会只有一次，同样的，异常处理也只会有一次
     * 
     * @param exc
     */
    public void catchThrowable(Throwable exc)
    {
        if (serverChannel.closeChannel())
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
            // serverChannel.closeChannel();
            // 调用这个方法是为了让readHandler中的fail方法有机会执行。进而进行iobuf的释放
            notifyRead();
        }
    }
    
    public void doRead()
    {
        try
        {
            frameAndHandle();
        }
        catch (Throwable e)
        {
            logger.error("未预料的异常", e);
            catchThrowable(e);
            // 上面的方法中，由于此时仍然持有读取控制权，因此不会注册读取，也就不会走到fail方法。所以这里要执行关闭。由于采用cas防御性的编程，所以不需要担心重复释放的问题
            iobufReleaseState.close();
            return;
        }
    }
    
    public void frameAndHandle() throws Throwable
    {
        do
        {
            if (cursor < wrap)
            {
                DecodeResult decodeResult = frameDecodec.decodec(ioBuf);
                switch (decodeResult.getType())
                {
                    case LESS_THAN_PROTOCOL:
                        readAndWait();
                        return;
                    case BUF_NOT_ENOUGH:
                        ioBuf.compact().ensureCapacity(decodeResult.getNeed());
                        continueRead();
                        return;
                    case NOT_FIT_PROTOCOL:
                        logger.debug("协议错误，关闭链接");
                        catchThrowable(NotFitProtocolException.instance);
                        return;
                    case NORMAL:
                        Object intermediateResult = decodeResult.getBuf();
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
                        break;
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
                long _wrap = writeHandler.cursor() + capacity;
                if (cursor >= _wrap)
                {
                    return;
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
                        return;
                    }
                }
            }
        } while (true);
    }
    
    /**
     * 开始空闲读取等待，并且将倒数计时状态重置为false
     */
    public void readAndWait()
    {
        startCountdown = false;
        try
        {
            serverChannel.getSocketChannel().read(getWriteBuffer(), waitTimeout, TimeUnit.MILLISECONDS, serverChannel, this);
        }
        catch (Exception e)
        {
            catchThrowable(e);
            iobufReleaseState.close();
        }
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
    private void continueRead()
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
            // if (ioBuf.remainRead() > 0)
            // {
            // doRead();
            // }
            // else
            // {
            // readAndWait();
            // }
            serverChannel.getSocketChannel().read(activeFlag, serverChannel, this);
        }
    }
    
    @Override
    public long cursor()
    {
        return cursor;
    }
    
}
