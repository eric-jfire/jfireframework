package com.jfireframework.jnet.server.CompletionHandler;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.CompositeByteBuf;
import com.jfireframework.baseutil.concurrent.MPSCLinkedQueue;
import com.jfireframework.baseutil.concurrent.UnsafeReferenceFieldUpdater;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.jnet.common.channel.impl.ServerChannel;

public class UnOrderedWriteCompletionHandler implements WriteCompletionHandler
{
    private static final Logger                                                                        logger                      = ConsoleLogFactory.getLogger();
    private final ReadCompletionHandler                                                                readCompletionHandler;
    private final ServerChannel                                                                        serverChannel;
    private volatile long                                                                              cursor                      = 0;
    private volatile WritePermission                                                                   writePermission             = WritePermission.valueOf(WritePermission.UN_take, 0);
    private static final UnsafeReferenceFieldUpdater<UnOrderedWriteCompletionHandler, WritePermission> updater                     = new UnsafeReferenceFieldUpdater<>(UnOrderedWriteCompletionHandler.class, "writePermission");
    private final MPSCLinkedQueue<ByteBuf<?>>                                                          bufQueue                    = new MPSCLinkedQueue<ByteBuf<?>>();
    private final int                                                                                  maxBatchWriteNum;
    private final BatchWriteCompletionHandler                                                          batchWriteCompletionHandler = new BatchWriteCompletionHandler();
    
    private static final class WritePermission
    {
        public static final int UN_take = 0;
        public static final int TAKED   = 1;
        private final long      version;
        private final int       state;
        
        private WritePermission(int state, long version)
        {
            this.version = version;
            this.state = state;
        }
        
        static WritePermission valueOf(int state, long version)
        {
            return new WritePermission(state, version);
        }
        
    }
    
    public UnOrderedWriteCompletionHandler(ReadCompletionHandler readCompletionHandler, ServerChannel channelInfo, int maxBatchWriteNum)
    {
        this.maxBatchWriteNum = maxBatchWriteNum;
        this.readCompletionHandler = readCompletionHandler;
        this.serverChannel = channelInfo;
    }
    
    public void askToWrite(ByteBuf<?> buf)
    {
        
        WritePermission current = writePermission;
        bufQueue.offer(buf);
        if (current.state == WritePermission.UN_take && current == writePermission)
        {
            // 如果这个cas可以完成，就意味着这两个if之间，没有其他的操作参与进来
            if (updater.compareAndSwap(this, current, WritePermission.valueOf(WritePermission.TAKED, current.version)))
            {
                ByteBuf<?> bufForWrite = bufQueue.poll();
                serverChannel.getSocketChannel().write(bufForWrite.cachedNioBuffer(), 10, TimeUnit.SECONDS, bufForWrite, this);
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
        ByteBuffer buffer = buf.cachedNioBuffer();
        if (buffer.hasRemaining())
        {
            serverChannel.getSocketChannel().write(buffer, 10, TimeUnit.SECONDS, buf, this);
            return;
        }
        buf.release();
        writeNextInBatch();
    }
    
    private void writeNextInBatch()
    {
        try
        {
            cursor += 1;
            readCompletionHandler.reStartRead();
            if (bufQueue.isEmpty() == false)
            {
                int count = 0;
                CompositeByteBuf compositeByteBuf = new CompositeByteBuf();
                ByteBuf<?> nextBuf = null;
                while (count < maxBatchWriteNum && (nextBuf = bufQueue.poll()) != null)
                {
                    compositeByteBuf.addBuf(nextBuf);
                    count += 1;
                }
                cursor += count - 1;
                serverChannel.getSocketChannel().write(compositeByteBuf.nioBuffers(), 0, count, 10, TimeUnit.SECONDS, compositeByteBuf, batchWriteCompletionHandler);
            }
            else
            {
                WritePermission give_up = WritePermission.valueOf(WritePermission.UN_take, cursor);
                writePermission = give_up;
                if (bufQueue.isEmpty() == false)
                {
                    if (updater.compareAndSwap(this, give_up, WritePermission.valueOf(WritePermission.TAKED, give_up.version)))
                    {
                        ByteBuf<?> nextBuf = bufQueue.poll();
                        serverChannel.getSocketChannel().write(nextBuf.cachedNioBuffer(), 10, TimeUnit.SECONDS, nextBuf, this);
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
        }
        catch (Exception e)
        {
            e.printStackTrace();
            readCompletionHandler.catchThrowable(e);
        }
    }
    
    @Override
    public void failed(Throwable exc, ByteBuf<?> buf)
    {
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
                serverChannel.getSocketChannel().write(buffers, index, buffers.length - index, 10, TimeUnit.SECONDS, composeteByteBuf, this);
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
