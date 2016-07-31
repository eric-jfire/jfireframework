package com.jfireframework.jnet.server.CompletionHandler.weapon.capacity.async;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.DirectByteBuf;
import com.jfireframework.baseutil.concurrent.CpuCachePadingInt;
import com.jfireframework.baseutil.concurrent.CpuCachePadingLong;
import com.jfireframework.baseutil.disruptor.Disruptor;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.jnet.common.channel.impl.ServerChannel;
import com.jfireframework.jnet.common.decodec.FrameDecodec;
import com.jfireframework.jnet.common.exception.BufNotEnoughException;
import com.jfireframework.jnet.common.exception.LessThanProtocolException;
import com.jfireframework.jnet.common.exception.NotFitProtocolException;
import com.jfireframework.jnet.common.handler.DataHandler;
import com.jfireframework.jnet.common.result.WeaponTask;
import com.jfireframework.jnet.server.CompletionHandler.weapon.capacity.sync.WeaponSyncWriteHandler;
import com.jfireframework.jnet.server.CompletionHandler.weapon.capacity.sync.WeaponSyncWriteHandlerImpl;

public class WeaponAsyncReadHandlerImpl implements WeaponAsyncReadHandler
{
    private static final Logger          logger         = ConsoleLogFactory.getLogger();
    private final FrameDecodec           frameDecodec;
    private final DataHandler[]          handlers;
    private final DirectByteBuf          ioBuf          = DirectByteBuf.allocate(100);
    private final ServerChannel          serverChannel;
    private final static int             WORK           = 1;
    private final static int             IDLE           = 2;
    /**
     * 本线程仍然持有控制权
     */
    private final static int             ON_CONTROL     = 1;
    
    /**
     * 本线程让渡出控制权
     */
    private final static int             YIDLE          = 2;
    private final CpuCachePadingInt      readState      = new CpuCachePadingInt(IDLE);
    // 读取超时时间
    private final long                   readTimeout;
    private final long                   waitTimeout;
    // 最后一次读取时间
    private long                         lastReadTime;
    // 本次读取的截止时间
    private long                         endReadTime;
    // 启动读取超时的计数
    private boolean                      startCountdown = false;
    private final WeaponTask[]           tasks;
    private final int                    mask;
    // 下一个放入task的位置
    private CpuCachePadingLong           putSequenc     = new CpuCachePadingLong(0);
    private long                         wrapPut        = 0;
    // 下一个发送的位置
    private CpuCachePadingLong           sendSequence   = new CpuCachePadingLong(0);
    private long                         wrapSend       = 0;
    private final Disruptor              disruptor;
    private static final int             OUT_OF_PUBLISH = 0;
    private static final int             IN_PUBLISH     = 1;
    private final CpuCachePadingInt      publishState   = new CpuCachePadingInt(OUT_OF_PUBLISH);
    private final WeaponSyncWriteHandler writeHandler;
    
    public WeaponAsyncReadHandlerImpl(ServerChannel serverChannel, Disruptor disruptor)
    {
        writeHandler = new WeaponSyncWriteHandlerImpl(serverChannel, this);
        this.serverChannel = serverChannel;
        frameDecodec = serverChannel.getFrameDecodec();
        handlers = serverChannel.getHandlers();
        readTimeout = serverChannel.getReadTimeout();
        waitTimeout = serverChannel.getWaitTimeout();
        int capacity = 1;
        while (capacity < serverChannel.capacity())
        {
            capacity <<= 1;
        }
        tasks = new WeaponTask[capacity];
        for (int i = 0; i < tasks.length; i++)
        {
            tasks[i] = new WeaponTask();
        }
        mask = capacity - 1;
        this.disruptor = disruptor;
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
        while (true)
        {
            try
            {
                int result = frameAndHandle();
                if (result == ON_CONTROL)
                {
                    readAndWait(false);
                    return;
                }
                else
                {
                    return;
                }
            }
            catch (LessThanProtocolException e)
            {
                readAndWait(false);
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
                serverChannel.closeChannel();
                return;
            }
            catch (Throwable e)
            {
                catchThrowable(e);
                serverChannel.closeChannel();
                return;
            }
        }
    }
    
    private int frameAndHandle() throws Exception
    {
        while (true)
        {
            if (availablePut() == false)
            {
                readState.set(IDLE);
                if (availablePut() == false)
                {
                    return YIDLE;
                }
                else
                {
                    if (readState.compareAndSwap(IDLE, WORK))
                    {
                        continue;
                    }
                    else
                    {
                        return YIDLE;
                    }
                }
            }
            long put = putSequenc.value();
            Object intermediateResult = frameDecodec.decodec(ioBuf);
            WeaponTask task = tasks[(int) (put & mask)];
            task.setChannelInfo(serverChannel);
            task.setData(intermediateResult);
            task.setIndex(0);
            putSequenc.set(put + 1);
            if (publishState.value() == OUT_OF_PUBLISH && publishState.compareAndSwap(OUT_OF_PUBLISH, IN_PUBLISH))
            {
                disruptor.publish(this);
            }
            if (ioBuf.remainRead() == 0)
            {
                return ON_CONTROL;
            }
        }
        
    }
    
    private boolean availablePut()
    {
        if (putSequenc.value() < wrapPut)
        {
            return true;
        }
        wrapPut = sendSequence.value() + mask;
        if (putSequenc.value() < wrapPut)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    /**
     * 开始空闲读取等待，并且将倒数计时状态重置为false
     */
    public void readAndWait(boolean init)
    {
        if (init)
        {
            readState.set(WORK);
        }
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
    private long getRemainTime()
    {
        long time = endReadTime - lastReadTime;
        return time;
    }
    
    @Override
    public void notifyRead()
    {
        while (readState.value() == IDLE && readState.compareAndSwap(IDLE, WORK))
        {
            if (availablePut() == false)
            {
                readState.set(IDLE);
                if (availablePut() == false)
                {
                    if (publishState.compareAndSwap(OUT_OF_PUBLISH, IN_PUBLISH))
                    {
                        disruptor.publish(this);
                        return;
                    }
                    else
                    {
                        return;
                    }
                }
                else
                {
                    continue;
                }
            }
            else
            {
                if (ioBuf.remainRead() > 0)
                {
                    doRead();
                }
                else
                {
                    readAndWait(false);
                }
            }
        }
    }
    
    @Override
    public void asyncHandle()
    {
        try
        {
            while (writeHandler.availablePut())
            {
                long current = sendSequence.value();
                if (current >= wrapSend)
                {
                    wrapSend = putSequenc.value();
                }
                if (current < wrapSend)
                {
                    WeaponTask task = tasks[(int) (current & mask)];
                    Object intermediateResult = task.getData();
                    for (int i = 0; i < handlers.length;)
                    {
                        intermediateResult = handlers[i].handle(intermediateResult, task);
                        if (i == task.getIndex())
                        {
                            i++;
                            task.setIndex(i);
                        }
                        else
                        {
                            i = task.getIndex();
                        }
                    }
                    if (intermediateResult instanceof ByteBuf<?>)
                    {
                        // 上面已经判断过是否能放入了
                        writeHandler.trySend((ByteBuf<?>) intermediateResult);
                    }
                    sendSequence.set(current + 1);
                }
                else
                {
                    break;
                }
            }
            publishState.set(OUT_OF_PUBLISH);
            if (writeHandler.availablePut() == false)
            {
                return;
            }
            else
            {
                notifyRead();
            }
        }
        catch (Throwable e)
        {
            catchThrowable(e);
            serverChannel.closeChannel();
        }
    }
    
}
