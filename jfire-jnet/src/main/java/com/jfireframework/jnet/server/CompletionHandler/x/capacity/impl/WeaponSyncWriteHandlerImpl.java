package com.jfireframework.jnet.server.CompletionHandler.x.capacity.impl;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.concurrent.CpuCachePadingInt;
import com.jfireframework.baseutil.concurrent.CpuCachePadingLong;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.jnet.common.channel.impl.ServerChannel;
import com.jfireframework.jnet.server.CompletionHandler.x.capacity.WeaponReadHandler;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class WeaponSyncWriteHandlerImpl implements WeaponSyncWriteHandler
{
    
    public static class BufHolder
    {
        public long                 p1, p2, p3, p4, p5, p6, p7;
        public int                  p8;
        private volatile ByteBuf<?> buf;
        public int                  p9;
        public long                 p10, p11, p12, p13, p14, p15, p16;
        
        public ByteBuf<?> getBuf()
        {
            return buf;
        }
        
        public void setBuf(ByteBuf<?> buf)
        {
            this.buf = buf;
        }
        
        public long nouse()
        {
            return p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16;
        }
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
    protected BufHolder[]           bufArray;
    protected int                   lengthMask;
    protected int                   capacity         = 0;
    /**
     * 写出处理器下一个数据可以放入的地方
     * 注意，是下一个
     */
    private CpuCachePadingLong      putSequence      = new CpuCachePadingLong(0);
    /**
     * 写出处理器写一个发送数据的位置
     */
    private volatile long           sendSequence     = 0;
    private long                    wrapSendSequence = 0;
    private long                    wrapPutSequence  = 0;
    private final WeaponReadHandler weaponReadHandler;
    private final ServerChannel     serverChannel;
    private final int               idle             = 0;
    private final int               work             = 1;
    private final CpuCachePadingInt state            = new CpuCachePadingInt(idle);
    private final Logger            logger           = ConsoleLogFactory.getLogger();
    
    public WeaponSyncWriteHandlerImpl(ServerChannel serverChannel, WeaponReadHandler weaponReadHandler)
    {
        this.weaponReadHandler = weaponReadHandler;
        this.serverChannel = serverChannel;
        setCapacity(serverChannel.capacity());
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
        sendSequence += 1;
        weaponReadHandler.notifyRead();
        if (sendSequence >= wrapSendSequence)
        {
            wrapSendSequence = putSequence.value();
        }
        if (sendSequence < wrapSendSequence)
        {
            buf = getBuf(sendSequence);
            serverChannel.getSocketChannel().write(buf.cachedNioBuffer(), 10, TimeUnit.SECONDS, buf, this);
            return;
        }
        else
        {
            state.set(idle);
            wrapSendSequence = putSequence.value();
            if (sendSequence >= wrapSendSequence)
            {
                return;
            }
            else
            {
                // 假设程序这个时候走到这里，然后失去了cpu时间。读取处理器此时读取了数据进来，并且唤醒了写处理器。
                // 然后在别的线程中，写处理器将所有的数据都处理完毕了，再次回到idle状态。然后这个时候程序进去其实是没有数据可以写的
                getIntoWork();
            }
        }
    }
    
    @Override
    public void failed(Throwable exc, ByteBuf<?> buf)
    {
        logger.error("error", exc);
        buf.release();
        weaponReadHandler.catchThrowable(exc);
    }
    
    @Override
    public boolean trySend(ByteBuf<?> buf)
    {
        if (putSequence.value() < wrapPutSequence)
        {
            setBuf(buf, putSequence.value());
            putSequence.set(putSequence.value() + 1);
            getIntoWork();
            return true;
        }
        else
        {
            wrapPutSequence = sendSequence + lengthMask;
            if (putSequence.value() < wrapPutSequence)
            {
                setBuf(buf, putSequence.value());
                putSequence.set(putSequence.value() + 1);
                getIntoWork();
                return true;
            }
            else
            {
                return false;
            }
        }
    }
    
    private void getIntoWork()
    {
        while (state.value() == idle && state.compareAndSwap(idle, work))
        {
            long current = putSequence.value();
            if (sendSequence < current)
            {
                ByteBuf<?> buf = getBuf(sendSequence);
                serverChannel.getSocketChannel().write(buf.cachedNioBuffer(), 10, TimeUnit.SECONDS, buf, this);
                return;
            }
            else
            {
                state.set(idle);
                if (current == putSequence.value())
                {
                    return;
                }
                else
                {
                    continue;
                }
            }
        }
    }
    
    @Override
    public boolean noMoreSend()
    {
        return sendSequence == putSequence.value();
    }
}
