package com.jfireframework.jnet.server.CompletionHandler.weapon.capacity.sync.push;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.concurrent.CpuCachePadingInt;
import com.jfireframework.baseutil.concurrent.CpuCachePadingLong;
import com.jfireframework.baseutil.concurrent.MPSCLinkedQueue;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.jnet.common.channel.impl.ServerChannel;
import com.jfireframework.jnet.server.CompletionHandler.WeaponReadHandler;
import com.jfireframework.jnet.server.CompletionHandler.weapon.capacity.sync.WeaponCapacityWriteHandler;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class WeaponSyncWriteHandlerImpl implements WeaponCapacityWriteHandler
{
    
    public static class BufHolder
    {
        // public long p1, p2, p3, p4, p5, p6, p7;
        // public int p8;
        private volatile ByteBuf<?> buf;
        // public int p9;
        // public long p10, p11, p12, p13, p14, p15, p16;
        
        public ByteBuf<?> getBuf()
        {
            return buf;
        }
        
        public void setBuf(ByteBuf<?> buf)
        {
            this.buf = buf;
        }
        
        // public long nouse()
        // {
        // return p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 +
        // p13 + p14 + p15 + p16;
        // }
    }
    
    protected final static int    base;
    protected final static int    scale;
    protected static final Unsafe unsafe = ReflectUtil.getUnsafe();
    static
    {
        base = unsafe.arrayBaseOffset(BufHolder[].class);
        if (4 == unsafe.arrayIndexScale(BufHolder[].class))
        {
            scale = 2;
        }
        else if (8 == unsafe.arrayIndexScale(BufHolder[].class))
        {
            scale = 3;
        }
        else
        {
            throw new RuntimeException("错误的长度信息");
        }
    }
    protected BufHolder[]               bufArray;
    protected int                       lengthMask;
    protected int                       capacity         = 0;
    /**
     * 写出处理器下一个数据可以放入的地方
     * 注意，是下一个
     */
    private CpuCachePadingLong          putSequence      = new CpuCachePadingLong(0);
    /**
     * 写出处理器写一个发送数据的位置
     */
    // private volatile long sendSequence = 0;
    private CpuCachePadingLong          sendSequence     = new CpuCachePadingLong(0);
    private long                        wrapSendSequence = 0;
    private long                        wrapPutSequence  = 0;
    private final WeaponReadHandler     readHandler;
    private final ServerChannel         serverChannel;
    private final int                   idle             = 0;
    private final int                   work             = 1;
    private final CpuCachePadingInt     idleState        = new CpuCachePadingInt(idle);
    private final Logger                logger           = ConsoleLogFactory.getLogger();
    private MPSCLinkedQueue<ByteBuf<?>> asyncSendQueue   = new MPSCLinkedQueue<>();
    // 处于响应客户端请求并且回送数据的模式
    private static final int            response         = 0;
    // 处于主动推送消息给客户端的模式
    private static final int            push             = 1;
    private final CpuCachePadingInt     pushState        = new CpuCachePadingInt(response);
    
    public WeaponSyncWriteHandlerImpl(ServerChannel serverChannel, int capacity, WeaponReadHandler readHandler)
    {
        this.readHandler = readHandler;
        this.serverChannel = serverChannel;
        setCapacity(capacity);
    }
    
    private void setCapacity(int capacity)
    {
        Verify.True(capacity > 1, "数组的大小必须大于1");
        int tmp = 1;
        while (tmp < capacity)
        {
            tmp <<= 1;
        }
        this.capacity = tmp;
        bufArray = new BufHolder[this.capacity];
        for (int i = 0; i < tmp; i++)
        {
            bufArray[i] = new BufHolder();
        }
        lengthMask = tmp - 1;
    }
    
    public ByteBuf<?> getBuf(long cursor)
    {
        return ((BufHolder) unsafe.getObject(bufArray, base + ((cursor & lengthMask) << scale))).getBuf();
    }
    
    public void setBuf(ByteBuf<?> buf, long cursor)
    {
        ((BufHolder) unsafe.getObject(bufArray, base + ((cursor & lengthMask) << scale))).setBuf(buf);
    }
    
    @Override
    public void completed(Integer result, ByteBuf<?> buf)
    {
        ByteBuffer buffer = buf.cachedNioBuffer();
        if (buffer.hasRemaining())
        {
            serverChannel.getSocketChannel().write(buffer, 10, TimeUnit.SECONDS, buf, this);
            return;
        }
        buf.release();
        if (pushState.value() == push)
        {
            buf = asyncSendQueue.poll();
            if (buf != null)
            {
                serverChannel.getSocketChannel().write(buf.cachedNioBuffer(), 10, TimeUnit.SECONDS, buf, this);
                return;
            }
            else
            {
                pushState.set(response);
            }
        }
        else
        {
            // sendSequence += 1;
            sendSequence.set(sendSequence.value() + 1);
            readHandler.notifyRead();
        }
        long current = availableSend();
        if (current != -1)
        {
            buf = getBuf(current);
            serverChannel.getSocketChannel().write(buf.cachedNioBuffer(), 10, TimeUnit.SECONDS, buf, this);
            return;
        }
        else
        {
            idleState.set(idle);
            // 假设程序这个时候走到这里，然后失去了cpu时间。读取处理器此时读取了数据进来，并且唤醒了写处理器。
            // 然后在别的线程中，写处理器将所有的数据都处理完毕了，再次回到idle状态。然后这个时候程序进去其实是没有数据可以写的
            retryWrite();
        }
    }
    
    @Override
    public void failed(Throwable exc, ByteBuf<?> buf)
    {
        logger.error("error", exc);
        buf.release();
        readHandler.catchThrowable(exc);
    }
    
    public void write(ByteBuf<?> buf, long index)
    {
        setBuf(buf, index);
        putSequence.set(index + 1);
        retryWrite();
    }
    
    private long availableSend()
    {
        long current = sendSequence.value();
        if (current < wrapSendSequence)
        {
            return current;
        }
        wrapSendSequence = putSequence.value();
        if (current < wrapSendSequence)
        {
            return current;
        }
        else
        {
            return -1;
        }
    }
    
    @Override
    public long availablePut()
    {
        long current = putSequence.value();
        if (current < wrapPutSequence)
        {
            return current;
        }
        else
        {
            wrapPutSequence = sendSequence.value() + lengthMask;
            if (current < wrapPutSequence)
            {
                return current;
            }
            else
            {
                return -1;
            }
        }
    }
    
    private void retryWrite()
    {
        while (true)
        {
            if (idleState.value() == idle && idleState.compareAndSwap(idle, work))
            {
                wrapSendSequence = putSequence.value();
                long current = sendSequence.value();
                if (current < wrapSendSequence)
                {
                    ByteBuf<?> buf = getBuf(current);
                    pushState.set(response);
                    serverChannel.getSocketChannel().write(buf.cachedNioBuffer(), 10, TimeUnit.SECONDS, buf, this);
                    return;
                }
                else
                {
                    ByteBuf<?> buf = asyncSendQueue.poll();
                    if (buf != null)
                    {
                        pushState.set(push);
                        serverChannel.getSocketChannel().write(buf.cachedNioBuffer(), 10, TimeUnit.SECONDS, buf, this);
                        return;
                    }
                    else
                    {
                        idleState.set(idle);
                        if (wrapSendSequence != putSequence.value())
                        {
                            continue;
                        }
                        else if (asyncSendQueue.isEmpty() == false)
                        {
                            continue;
                        }
                        else
                        {
                            return;
                        }
                    }
                }
            }
            else
            {
                return;
            }
        }
    }
    
    @Override
    public void push(ByteBuf<?> buf)
    {
        asyncSendQueue.add(buf);
        retryWrite();
    }
    
    @Override
    public void write(ByteBuf<?> buf)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public long cursor()
    {
        // TODO Auto-generated method stub
        return 0;
    }
    
}
