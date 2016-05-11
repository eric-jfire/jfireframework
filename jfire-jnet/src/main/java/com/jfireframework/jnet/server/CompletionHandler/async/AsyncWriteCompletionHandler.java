package com.jfireframework.jnet.server.CompletionHandler.async;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.concurrent.MPSCLinkedQueue;
import com.jfireframework.baseutil.disruptor.Sequence;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.jnet.common.channel.ServerChannelInfo;
import sun.misc.Unsafe;

public class AsyncWriteCompletionHandler implements CompletionHandler<Integer, ByteBuf<?>>
{
    private final AsyncReadCompletionHandler  readCompletionHandler;
    private final ServerChannelInfo           channelInfo;
    private volatile long                     cursor   = 0;
    private final static long                 _cursor  = ReflectUtil.getFieldOffset("cursor", AsyncWriteCompletionHandler.class);
    private final static Unsafe               unsafe   = ReflectUtil.getUnsafe();
    private static final int                  UN_WRITE = 0;
    private static final int                  WRITING  = 1;
    private AtomicInteger                     status   = new AtomicInteger(UN_WRITE);
    private final MPSCLinkedQueue<ByteBuf<?>> bufqueue = new MPSCLinkedQueue<ByteBuf<?>>();
    
    public AsyncWriteCompletionHandler(AsyncReadCompletionHandler readCompletionHandler, ServerChannelInfo channelInfo)
    {
        this.readCompletionHandler = readCompletionHandler;
        this.channelInfo = channelInfo;
    }
    
    public void putResult(ByteBuf<?> obj)
    {
        bufqueue.add(obj);
    }
    
    public void askToWrite()
    {
        int s = status.get();
        if (s == UN_WRITE)
        {
            if (bufqueue.isEmpty() == false)
            {
                if (status.compareAndSet(UN_WRITE, WRITING))
                {
                    ByteBuf<?> buf = bufqueue.poll();
                    channelInfo.getChannel().write(buf.cachedNioBuffer(), 10, TimeUnit.SECONDS, buf, this);
                }
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
            ByteBuffer buffer = buf.cachedNioBuffer();
            if (buffer.hasRemaining())
            {
                channelInfo.getChannel().write(buffer, 10, TimeUnit.SECONDS, buf, this);
                return;
            }
            else
            {
                // unsafe.putOrderedLong(this, _cursor, cursor + 1);
                cursor += 1;
                readCompletionHandler.reStartRead();
                if (bufqueue.isEmpty() == false)
                {
                    ByteBuf<?> nextBuf = bufqueue.poll();
                    channelInfo.getChannel().write(nextBuf.cachedNioBuffer(), 10, TimeUnit.SECONDS, nextBuf, this);
                }
                else
                {
                    status.set(UN_WRITE);
                    if (bufqueue.isEmpty() == false)
                    {
                        if (status.compareAndSet(UN_WRITE, WRITING))
                        {
                            ByteBuf<?> nextBuf = bufqueue.poll();
                            channelInfo.getChannel().write(nextBuf.cachedNioBuffer(), 10, TimeUnit.SECONDS, nextBuf, this);
                        }
                        else
                        {
                            ;
                        }
                    }
                    else
                    {
                        ;
                    }
                }
                // 这一步最无关紧要，也不太可能引起异常，放到最后一步执行，这样也避免下面异常捕获的时候出现释放两次的情况
                buf.release();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            buf.release();
            readCompletionHandler.catchThrowable(e);
        }
    }
    
    @Override
    public void failed(Throwable exc, ByteBuf<?> buf)
    {
        buf.release();
        readCompletionHandler.catchThrowable(exc);
    }
}
