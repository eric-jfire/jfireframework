package com.jfireframework.jnet.server.CompletionHandler.async;

import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.disruptor.CpuCachePadingValue;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.jnet.common.channel.ServerChannelInfo;

public class AsyncWriteCompletionHandler implements CompletionHandler<Integer, ByteBuf<?>>
{
    private final AsyncReadCompletionHandler       readCompletionHandler;
    private final ServerChannelInfo                channelInfo;
    private volatile long                          cursor    = 0;
    protected final int                            resultArrayLengthMask;
    private static final int                       UN_WRITE  = 0;
    private static final int                       WRITING   = 1;
    private AtomicInteger                          status    = new AtomicInteger(UN_WRITE);
    private final AtomicReferenceArray<ByteBuf<?>> bufArray;
    private final CpuCachePadingValue              preCursor = new CpuCachePadingValue();
    
    public AsyncWriteCompletionHandler(AsyncReadCompletionHandler readCompletionHandler, ServerChannelInfo channelInfo)
    {
        this.readCompletionHandler = readCompletionHandler;
        this.channelInfo = channelInfo;
        int resultArrayLength = channelInfo.getEntryArraySize();
        Verify.True(resultArrayLength > 1, "数组的大小必须大于1");
        Verify.True(Integer.bitCount(resultArrayLength) == 1, "数组的大小必须是2的次方幂");
        bufArray = new AtomicReferenceArray<>(resultArrayLength);
        resultArrayLengthMask = resultArrayLength - 1;
    }
    
    public void putResult(ByteBuf<?> obj)
    {
        long point = preCursor.next();
        bufArray.set((int) (point & resultArrayLengthMask), obj);
    }
    
    public void askToWrite()
    {
        int s = status.get();
        if (s == UN_WRITE)
        {
            if (status.compareAndSet(UN_WRITE, WRITING))
            {
                ByteBuf<?> buf = bufArray.get((int) (cursor & resultArrayLengthMask));
                channelInfo.getChannel().write(buf.nioBuffer(), 10, TimeUnit.SECONDS, buf, this);
            }
        }
    }
    
    public long cursor()
    {
        return cursor;
    }
    
    @Override
    public void completed(Integer writeTotal, ByteBuf<?> buf)
    {
        try
        {
            buf.addReadIndex(writeTotal);
            if (buf.remainRead() > 0)
            {
                channelInfo.getChannel().write(buf.nioBuffer(), 10, TimeUnit.SECONDS, buf, this);
                return;
            }
            else
            {
                cursor += 1;
                readCompletionHandler.reStartRead();
                if (readCompletionHandler.isAvailable(cursor))
                {
                    ByteBuf<?> nextBuf = bufArray.get((int) (cursor & resultArrayLengthMask));
                    channelInfo.getChannel().write(nextBuf.nioBuffer(), 10, TimeUnit.SECONDS, buf, this);
                }
                else
                {
                    while (shouldStopWrite() == false)
                    {
                        if (readCompletionHandler.isAvailable(cursor))
                        {
                            if (status.compareAndSet(UN_WRITE, WRITING))
                            {
                                ByteBuf<?> nextBuf = bufArray.get((int) (cursor & resultArrayLengthMask));
                                channelInfo.getChannel().write(nextBuf.nioBuffer(), 10, TimeUnit.SECONDS, buf, this);
                            }
                        }
                    }
                    
                }
                // 这一步最无关紧要，也不太可能引起异常，放到最后一步执行，这样也避免下面异常捕获的时候出现释放两次的情况
                buf.release();
            }
        }
        catch (Exception e)
        {
            buf.release();
            readCompletionHandler.catchThrowable(e);
        }
    }
    
    private boolean shouldStopWrite()
    {
        int s = status.get();
        if (s == UN_WRITE)
        {
            return true;
        }
        status.set(UN_WRITE);
        return false;
    }
    
    @Override
    public void failed(Throwable exc, ByteBuf<?> buf)
    {
        buf.release();
        readCompletionHandler.catchThrowable(exc);
    }
}
