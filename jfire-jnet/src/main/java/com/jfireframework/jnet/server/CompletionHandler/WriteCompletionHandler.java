package com.jfireframework.jnet.server.CompletionHandler;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.jnet.common.channel.ServerChannelInfo;
import com.jfireframework.jnet.common.result.ServerInternalResult;

@Resource
public class WriteCompletionHandler implements CompletionHandler<Integer, ByteBuf<?>>
{
    private volatile long           cursor       = 0;
    private ReadCompletionHandler   readCompletionHandler;
    private long                    wrapPoint    = 0;
    private static final int        retryPermit  = 2;
    private final ServerChannelInfo channelInfo;
    private final int               batchMode;
    public static final int         batch_write  = 1;
    public static final int         single_write = 0;
    
    public WriteCompletionHandler(ReadCompletionHandler readCompletionHandler, ServerChannelInfo channelInfo, int batchmode)
    {
        this.readCompletionHandler = readCompletionHandler;
        this.channelInfo = channelInfo;
        this.batchMode = batchmode;
    }
    
    public long cursor()
    {
        return cursor;
    }
    
    @Override
    public void completed(Integer writeTotal, ByteBuf<?> buf)
    {
        if (batchMode == single_write)
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
                    long version = cursor + 1;
                    int tryCount = 0;
                    while (true)
                    {
                        /**
                         * 首先需要判断下一个要写入的序号是不是小于已经读取的序号。只有小于的情况下才能提取数据进行写出操作
                         */
                        if (version < wrapPoint)
                        {
                            ServerInternalResult next = (ServerInternalResult) channelInfo.getResult(version);
                            // 由于写操作的序号没有前进，此时可以调用tryWrite来尝试直接获得写出许可，只要数据被处理完毕。
                            if (next.tryWrite(version))
                            {
                                cursor = version;
                                // 重启读取必须在更新了cursor之后，否则因为没有下一个可以容纳的空间，一重启读取又进入了等待读取状态。
                                // 由于上一步更新了cursor，所以下面的操作都存在并发的可能性，那么重启读取要保证只能被一个线程真正激发一次。否则就会造成多重读取异常。这依靠方法内的cas完成。
                                readCompletionHandler.reStartRead();
                                // 该结果已经在上面的操作中被获得了写出许可，因此这里可以直接将数据写出。
                                next.directWrite();
                            }
                            else
                            {
                                cursor = version;
                                readCompletionHandler.reStartRead();
                                next.write(version);
                            }
                            buf.release();
                            return;
                        }
                        tryCount += 1;
                        if (tryCount < retryPermit)
                        {
                            wrapPoint = readCompletionHandler.cursor();
                        }
                        else
                        {
                            break;
                        }
                    }
                    cursor = version;
                    readCompletionHandler.reStartRead();
                    wrapPoint = readCompletionHandler.cursor();
                    // 一定要尝试写下一个。
                    // 否则的话，因为写完成器的版本号没有更新，而其他线程尝试失败，写完成又不写下一个的话，就会导致数据没有线程要写出，进而活锁。
                    if (version < wrapPoint)
                    {
                        ServerInternalResult next = (ServerInternalResult) channelInfo.getResult(version);
                        next.write(version);
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
        else
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
                    long version = cursor + 1;
                    int tryCount = 0;
                    while (true)
                    {
                        if (version < wrapPoint)
                        {
                            ServerInternalResult next = (ServerInternalResult) channelInfo.getResult(version);
                            if (next.tryWrite(version))
                            {
                                ByteBuf<?> composi = (ByteBuf<?>) next.getData();
                                while (true)
                                {
                                    version += 1;
                                    if (version < wrapPoint)
                                    {
                                        next = (ServerInternalResult) channelInfo.getResult(version);
                                        if (next.tryWrite(version) == false)
                                        {
                                            version -= 1;
                                            break;
                                        }
                                        composi.put((ByteBuf<?>) next.getData());
                                        // 这边要进行释放，否则的话就没有线程操作会来释放的
                                        ((ByteBuf<?>) next.getData()).release();
                                    }
                                    else
                                    {
                                        version -= 1;
                                        tryCount += 1;
                                        if (tryCount < retryPermit)
                                        {
                                            wrapPoint = readCompletionHandler.cursor();
                                        }
                                        else
                                        {
                                            break;
                                        }
                                    }
                                }
                                cursor = version;
                                readCompletionHandler.reStartRead();
                                channelInfo.getChannel().write(composi.cachedNioBuffer(), 10, TimeUnit.SECONDS, composi, this);
                            }
                            else
                            {
                                cursor = version;
                                readCompletionHandler.reStartRead();
                                next.write(version);
                            }
                            buf.release();
                            return;
                        }
                        tryCount += 1;
                        if (tryCount < retryPermit)
                        {
                            wrapPoint = readCompletionHandler.cursor();
                        }
                        else
                        {
                            break;
                        }
                    }
                    cursor = version;
                    readCompletionHandler.reStartRead();
                    wrapPoint = readCompletionHandler.cursor();
                    if (version < wrapPoint)
                    {
                        ServerInternalResult next = (ServerInternalResult) channelInfo.getResult(version);
                        next.write(version);
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
    }
    
    @Override
    public void failed(Throwable exc, ByteBuf<?> buf)
    {
        buf.release();
        readCompletionHandler.catchThrowable(exc);
    }
    
}
