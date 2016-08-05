package com.jfireframework.jnet.server.CompletionHandler.weapon.capacity.sync.write.withoutpush;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.concurrent.CpuCachePadingInt;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.jnet.common.channel.impl.ServerChannel;
import com.jfireframework.jnet.server.CompletionHandler.weapon.capacity.sync.WeaponCapacityReadHandler;
import com.jfireframework.jnet.server.CompletionHandler.weapon.capacity.sync.WeaponCapacityWriteHandler;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class WeaponCapacityWriteHandlerImpl implements WeaponCapacityWriteHandler
{
    
    protected abstract class left
    {
        protected long p1, p2, p3, p4, p5, p6, p7;
        protected int  p8;
    }
    
    protected abstract class value extends left
    {
        protected volatile ByteBuf<?> buf;
    }
    
    protected class BufHolder extends value
    {
        
        protected int  p9;
        protected long p10, p11, p12, p13, p14, p15, p16;
        
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
    protected BufHolder[]                   bufArray;
    protected int                           lengthMask;
    private volatile long                   cursor    = 0;
    private long                            wrap      = 0;
    protected int                           capacity  = 0;
    private final WeaponCapacityReadHandler readHandler;
    private final ServerChannel             serverChannel;
    private final int                       idle      = 0;
    private final int                       work      = 1;
    private final CpuCachePadingInt         idleState = new CpuCachePadingInt(idle);
    private final Logger                    logger    = ConsoleLogFactory.getLogger();
    
    public WeaponCapacityWriteHandlerImpl(ServerChannel serverChannel, int capacity, WeaponCapacityReadHandler readHandler)
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
        cursor += 1;
        if (cursor < wrap)
        {
            buf = getBuf(cursor);
            serverChannel.getSocketChannel().write(buf.cachedNioBuffer(), 10, TimeUnit.SECONDS, buf, this);
            return;
        }
        else
        {
            do
            {
                wrap = readHandler.cursor();
                if (cursor < wrap)
                {
                    buf = getBuf(cursor);
                    serverChannel.getSocketChannel().write(buf.cachedNioBuffer(), 10, TimeUnit.SECONDS, buf, this);
                    return;
                }
                else
                {
                    readHandler.notifyRead();
                    idleState.set(idle);
                    wrap = readHandler.cursor();
                    if (cursor < wrap)
                    {
                        if (idleState.compareAndSwap(idle, work))
                        {
                            continue;
                        }
                        else
                        {
                            return;
                        }
                    }
                    else
                    {
                        return;
                    }
                }
            } while (true);
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
        if (idleState.value() == idle && idleState.compareAndSwap(idle, work))
        {
            buf = getBuf(cursor);
            serverChannel.getSocketChannel().write(buf.cachedNioBuffer(), 10, TimeUnit.SECONDS, buf, this);
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
    
}
