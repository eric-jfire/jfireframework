package com.jfireframework.jnet2.server.CompletionHandler.weapon.capacity.sync.write.push;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.concurrent.CpuCachePadingInt;
import com.jfireframework.baseutil.concurrent.CpuCachePadingLong;
import com.jfireframework.baseutil.concurrent.MPSCQueue;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.jnet2.common.channel.impl.ServerChannel;
import com.jfireframework.jnet2.server.CompletionHandler.weapon.capacity.common.BufHolder;
import com.jfireframework.jnet2.server.CompletionHandler.weapon.capacity.sync.CapacityReadHandler;
import com.jfireframework.jnet2.server.CompletionHandler.weapon.capacity.sync.WeaponCapacityWriteHandler;

public final class WeaponCapacityWriteWithPushHandlerImpl implements WeaponCapacityWriteHandler
{
    
    private final BufHolder[]               bufArray;
    private int                             lengthMask;
    // 下一个要写出的buf的序号
    private volatile long                   cursor            = 0;
    private long                            wrap              = 0;
    /**
     * 代表着已经被写入的序号，所以使用的时候，wrap的值应该是该属性的值+1
     */
    private final CpuCachePadingLong        writeCursor       = new CpuCachePadingLong(-1);
    private final CapacityReadHandler       readHandler;
    private final int                       idle              = 0;
    private final int                       work              = 1;
    private final CpuCachePadingInt         idleState         = new CpuCachePadingInt(idle);
    private static final Logger             logger            = ConsoleLogFactory.getLogger();
    private final BatchWriteHandler         batchWriteHandler = new BatchWriteHandler();
    private final AsynchronousSocketChannel socketChannel;
    private final ByteBuffer[]              batchBuffers;
    private final ByteBuf<?>[]              batchBufs;
    private MPSCQueue<ByteBuf<?>>           asyncSendQueue    = new MPSCQueue<>();
    // 处于响应客户端请求并且回送数据的模式
    private static final int                response          = 0;
    // 处于主动推送消息给客户端的模式
    private static final int                push              = 1;
    private final CpuCachePadingInt         pushState         = new CpuCachePadingInt(response);
    
    public WeaponCapacityWriteWithPushHandlerImpl(ServerChannel serverChannel, int capacity, CapacityReadHandler readHandler)
    {
        this.readHandler = readHandler;
        socketChannel = serverChannel.getSocketChannel();
        bufArray = new BufHolder[capacity];
        for (int i = 0; i < capacity; i++)
        {
            bufArray[i] = new BufHolder();
        }
        batchBuffers = new ByteBuffer[capacity];
        batchBufs = new ByteBuf[capacity];
        lengthMask = capacity - 1;
    }
    
    private ByteBuf<?> getBuf(long cursor)
    {
        return bufArray[(int) (cursor & lengthMask)].getBuf();
    }
    
    private void setBuf(ByteBuf<?> buf, long cursor)
    {
        bufArray[(int) (cursor & lengthMask)].setBuf(buf);
    }
    
    @Override
    public void completed(Integer result, ByteBuf<?> buf)
    {
        ByteBuffer buffer = buf.cachedNioBuffer();
        if (buffer.hasRemaining())
        {
            socketChannel.write(buffer, 10, TimeUnit.SECONDS, buf, this);
            return;
        }
        buf.release();
        if (pushState.value() == push)
        {
            buf = asyncSendQueue.poll();
            if (buf != null)
            {
                socketChannel.write(buf.cachedNioBuffer(), 10, TimeUnit.SECONDS, buf, this);
                return;
            }
            else
            {
                pushState.set(response);
                writeNextBuf();
            }
        }
        else
        {
            cursor += 1;
            writeNextBuf();
        }
    }
    
    private void writeNextBuf()
    {
        if (cursor < wrap)
        {
            batchWrite();
            return;
        }
        else
        {
            wrap = writeCursor.value() + 1;
            do
            {
                if (cursor < wrap)
                {
                    batchWrite();
                    return;
                }
                else
                {
                    ByteBuf<?> buf = asyncSendQueue.poll();
                    if (buf != null)
                    {
                        pushState.set(push);
                        socketChannel.write(buf.cachedNioBuffer(), 10, TimeUnit.SECONDS, buf, this);
                        return;
                    }
                    readHandler.notifyRead();
                    idleState.set(idle);
                    long newestWrap = writeCursor.value() + 1;
                    if (cursor < newestWrap || asyncSendQueue.isEmpty() == false)
                    {
                        if (idleState.compareAndSwap(idle, work))
                        {
                            wrap = writeCursor.value() + 1;
                            continue;
                        }
                        else
                        {
                            break;
                        }
                    }
                    else
                    {
                        break;
                    }
                }
            } while (true);
        }
    }
    
    private void batchWrite()
    {
        int length = 0;
        ByteBuf<?> buf;
        for (long i = cursor; i < wrap; i++)
        {
            buf = getBuf(i);
            batchBufs[length] = buf;
            batchBuffers[length++] = buf.nioBuffer();
        }
        batchWriteHandler.length = length;
        socketChannel.write(batchBuffers, 0, length, 10, TimeUnit.SECONDS, batchBuffers, batchWriteHandler);
    }
    
    @Override
    public void failed(Throwable exc, ByteBuf<?> buf)
    {
        logger.error("error", exc);
        readHandler.catchThrowable(exc);
        buf.release();
    }
    
    @Override
    public void write(ByteBuf<?> buf, long index)
    {
        setBuf(buf, index);
        writeCursor.set(index);
        if (idleState.value() == idle && idleState.compareAndSwap(idle, work))
        {
            // 由于取得了所有权，所以可以更改。并且由于当前只有该写入者。所以wrap的最大值只可能是index+1
            wrap = index + 1;
            if (cursor < wrap)
            {
                buf = getBuf(cursor);
                socketChannel.write(buf.cachedNioBuffer(), 10, TimeUnit.SECONDS, buf, this);
            }
            else
            {
                // 这里不需要多次尝试。因为这个方法是由读取处理器调用。在调用的时候只有该写入者有权限，不会有别的地方还可以写入。所以放弃掉所有权之后，也不会有新的数据填充入。
                // 所以这边是可以直接放弃的
                idleState.set(idle);
            }
        }
    }
    
    @Override
    public long availablePut()
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void push(ByteBuf<?> buf)
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void write(ByteBuf<?> buf)
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public long cursor()
    {
        return cursor;
    }
    
    class BatchWriteHandler implements CompletionHandler<Long, ByteBuffer[]>
    {
        private int length;
        
        @Override
        public void completed(Long result, ByteBuffer[] buffers)
        {
            for (int i = 0; i < length; i++)
            {
                if (buffers[i].hasRemaining())
                {
                    socketChannel.write(buffers, i, length - i, 10, TimeUnit.SECONDS, buffers, this);
                    return;
                }
            }
            for (int i = 0; i < length; i++)
            {
                batchBufs[i].release();
            }
            cursor = wrap;
            writeNextBuf();
        }
        
        @Override
        public void failed(Throwable exc, ByteBuffer[] buffers)
        {
            logger.error("error", exc);
            for (int i = 0; i < length; i++)
            {
                batchBufs[i].release();
            }
            readHandler.catchThrowable(exc);
        }
        
    }
    
}
