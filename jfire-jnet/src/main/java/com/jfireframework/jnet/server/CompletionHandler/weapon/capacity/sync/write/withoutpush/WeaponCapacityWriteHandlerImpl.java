package com.jfireframework.jnet.server.CompletionHandler.weapon.capacity.sync.write.withoutpush;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.concurrent.CpuCachePadingInt;
import com.jfireframework.baseutil.concurrent.CpuCachePadingLong;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.jnet.common.channel.impl.ServerChannel;
import com.jfireframework.jnet.server.CompletionHandler.weapon.capacity.sync.WeaponCapacityReadHandler;
import com.jfireframework.jnet.server.CompletionHandler.weapon.capacity.sync.WeaponCapacityWriteHandler;
import sun.misc.Unsafe;

public final class WeaponCapacityWriteHandlerImpl implements WeaponCapacityWriteHandler
{
    private class BufHolder
    {
        protected volatile ByteBuf<?> buf;
        
        public ByteBuf<?> getBuf()
        {
            return buf;
        }
        
        public void setBuf(ByteBuf<?> buf)
        {
            this.buf = buf;
        }
        
    }
    
    private final static int    base;
    private final static int    scale;
    private static final Unsafe unsafe = ReflectUtil.getUnsafe();
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
    private final BufHolder[]               bufArray;
    private int                             lengthMask;
    private volatile long                   cursor      = 0;
    private long                            wrap        = 0;
    /**
     * 代表着已经被写入的序号，所以使用的时候，wrap的值应该是该属性的值+1
     */
    private final CpuCachePadingLong        writeCursor = new CpuCachePadingLong(-1);
//    private int                             capacity    = 0;
    private final WeaponCapacityReadHandler readHandler;
    private final ServerChannel             serverChannel;
    private final int                       idle        = 0;
    private final int                       work        = 1;
    private final CpuCachePadingInt         idleState   = new CpuCachePadingInt(idle);
    private static final Logger             logger      = ConsoleLogFactory.getLogger();
    
    public WeaponCapacityWriteHandlerImpl(ServerChannel serverChannel, int capacity, WeaponCapacityReadHandler readHandler)
    {
        this.readHandler = readHandler;
        this.serverChannel = serverChannel;
        bufArray = new BufHolder[capacity];
        for (int i = 0; i < capacity; i++)
        {
            bufArray[i] = new BufHolder();
        }
        lengthMask = capacity - 1;
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
            wrap = writeCursor.value() + 1;
            do
            {
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
                    wrap = writeCursor.value() + 1;
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
        writeCursor.set(index);
        if (idleState.value() == idle && idleState.compareAndSwap(idle, work))
        {
            wrap = index + 1;
            if (cursor < wrap)
            {
                buf = getBuf(cursor);
                serverChannel.getSocketChannel().write(buf.cachedNioBuffer(), 10, TimeUnit.SECONDS, buf, this);
            }
            else
            {
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
    
}
