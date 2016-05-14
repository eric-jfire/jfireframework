package com.jfireframework.jnet.server.CompletionHandler;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.CompositeByteBuf;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.jnet.common.channel.ServerChannelInfo;
import com.jfireframework.jnet.common.result.ServerInternalResult;

@Resource
public class WriteCompletionHandler implements CompletionHandler<Integer, ByteBuf<?>>
{
    private volatile long               cursor                      = 0;
    private ReadCompletionHandler       readCompletionHandler;
    private long                        wrapPoint                   = 0;
    private static final int            retryPermit                 = 2;
    private final ServerChannelInfo     channelInfo;
    private final int                   batchMode;
    // 如果客户端可以持续不断的向服务端发送消息，则适合批量写出这种模式
    public static final int             batch_write                 = 1;
    // 单次写这种适合客户端要和服务器一问一答的形式。并且客户端需要根据服务器的反馈来进行下一次发出。这种情况下，是不会存在批量写的问题。因为没有批量的数据要处理
    public static final int             single_write                = 0;
    private BatchWriteCompletionHandler batchWriteCompletionHandler = new BatchWriteCompletionHandler();
    private static final Logger         logger                      = ConsoleLogFactory.getLogger();
    
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
    
    private void doSingleWrite(Integer writeTotal, ByteBuf<?> buf)
    {
        ByteBuffer buffer = buf.cachedNioBuffer();
        if (buffer.hasRemaining())
        {
            channelInfo.getChannel().write(buffer, 10, TimeUnit.SECONDS, buf, this);
            return;
        }
        buf.release();
        try
        {
            long nextCursor = cursor + 1;
            int tryCount = 0;
            while (true)
            {
                /**
                 * 首先需要判断下一个要写入的序号是不是小于已经读取的序号。只有小于的情况下才能提取数据进行写出操作
                 */
                if (nextCursor < wrapPoint)
                {
                    ServerInternalResult next = (ServerInternalResult) channelInfo.getResult(nextCursor);
                    // 由于写操作的序号没有前进，此时可以调用tryWrite来尝试直接获得写出许可，只要数据被处理完毕。
                    if (next.tryWrite(nextCursor))
                    {
                        cursor = nextCursor;
                        // 重启读取必须在更新了cursor之后，否则因为没有下一个可以容纳的空间，一重启读取又进入了等待读取状态。
                        // 由于上一步更新了cursor，所以下面的操作都存在并发的可能性，那么重启读取要保证只能被一个线程真正激发一次。否则就会造成多重读取异常。这依靠方法内的cas完成。
                        readCompletionHandler.reStartRead();
                        // 该结果已经在上面的操作中被获得了写出许可，因此这里可以直接将数据写出。
                        next.directWrite();
                    }
                    else
                    {
                        cursor = nextCursor;
                        readCompletionHandler.reStartRead();
                        next.write(nextCursor);
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
            cursor = nextCursor;
            readCompletionHandler.reStartRead();
            wrapPoint = readCompletionHandler.cursor();
            // 一定要尝试写下一个。
            // 否则的话，因为写完成器的版本号没有更新，而其他线程尝试失败，写完成又不写下一个的话，就会导致数据没有线程要写出，进而活锁。
            if (nextCursor < wrapPoint)
            {
                ServerInternalResult next = (ServerInternalResult) channelInfo.getResultVolatile(nextCursor);
                next.write(nextCursor);
            }
        }
        catch (Exception e)
        {
            readCompletionHandler.catchThrowable(e);
        }
    }
    
    protected void doBatchWrite(Integer writeTotal, ByteBuf<?> buf)
    {
        ByteBuffer buffer = buf.cachedNioBuffer();
        if (buffer.hasRemaining())
        {
            channelInfo.getChannel().write(buffer, 10, TimeUnit.SECONDS, buf, this);
            return;
        }
        buf.release();
        writeNextInBatch();
    }
    
    private void writeNextInBatch()
    {
        try
        {
            long nextCursor = cursor + 1;
            int tryCount = 0;
            while (true)
            {
                if (nextCursor < wrapPoint)
                {
                    ServerInternalResult next = (ServerInternalResult) channelInfo.getResultVolatile(nextCursor);
                    if (next.tryWrite(nextCursor))
                    {
                        CompositeByteBuf compositeByteBuf = new CompositeByteBuf();
                        compositeByteBuf.addBuf((ByteBuf<?>) next.getData());
                        while (true)
                        {
                            nextCursor += 1;
                            if (nextCursor < wrapPoint)
                            {
                                next = (ServerInternalResult) channelInfo.getResultVolatile(nextCursor);
                                if (next.tryWrite(nextCursor) == false)
                                {
                                    nextCursor -= 1;
                                    break;
                                }
                                compositeByteBuf.addBuf((ByteBuf<?>) next.getData());
                            }
                            else
                            {
                                nextCursor -= 1;
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
                        cursor = nextCursor;
                        channelInfo.getChannel().write(compositeByteBuf.nioBuffers(), 0, compositeByteBuf.nioBuffers().length, 10, TimeUnit.SECONDS, compositeByteBuf, batchWriteCompletionHandler);
                    }
                    else
                    {
                        cursor = nextCursor;
                        readCompletionHandler.reStartRead();
                        next.write(nextCursor);
                    }
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
            cursor = nextCursor;
            readCompletionHandler.reStartRead();
            wrapPoint = readCompletionHandler.cursor();
            if (nextCursor < wrapPoint)
            {
                ServerInternalResult next = (ServerInternalResult) channelInfo.getResultVolatile(nextCursor);
                next.write(nextCursor);
            }
        }
        catch (Exception e)
        {
            readCompletionHandler.catchThrowable(e);
        }
    }
    
    @Override
    public void completed(Integer writeTotal, ByteBuf<?> buf)
    {
        if (batchMode == single_write)
        {
            doSingleWrite(writeTotal, buf);
        }
        else
        {
            doBatchWrite(writeTotal, buf);
        }
    }
    
    @Override
    public void failed(Throwable exc, ByteBuf<?> buf)
    {
        logger.error("error", exc);
        buf.release();
        readCompletionHandler.catchThrowable(exc);
    }
    
    class BatchWriteCompletionHandler implements CompletionHandler<Long, CompositeByteBuf>
    {
        
        @Override
        public void completed(Long result, CompositeByteBuf composeteByteBuf)
        {
            ByteBuffer[] buffers = composeteByteBuf.nioBuffers();
            int index = 0;
            for (; index < buffers.length; index++)
            {
                if (buffers[index].hasRemaining())
                {
                    break;
                }
            }
            if (index == buffers.length)
            {
                composeteByteBuf.release();
                writeNextInBatch();
            }
            else
            {
                channelInfo.getChannel().write(buffers, index, buffers.length - index, 10, TimeUnit.SECONDS, composeteByteBuf, this);
            }
        }
        
        @Override
        public void failed(Throwable exc, CompositeByteBuf composeteByteBuf)
        {
            logger.error("error", exc);
            composeteByteBuf.release();
            readCompletionHandler.catchThrowable(exc);
        }
        
    }
    
}
