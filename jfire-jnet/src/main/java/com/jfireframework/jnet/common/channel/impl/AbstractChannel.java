package com.jfireframework.jnet.common.channel.impl;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.atomic.AtomicInteger;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.jnet.common.channel.JnetChannel;
import com.jfireframework.jnet.common.decodec.FrameDecodec;
import com.jfireframework.jnet.common.handler.DataHandler;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public abstract class AbstractChannel implements JnetChannel
{
    public final static int             OPEN        = 1;
    public final static int             CLOSE       = 2;
    // 消息通道的打开状态
    protected AtomicInteger             openState   = new AtomicInteger(OPEN);
    protected AsynchronousSocketChannel socketChannel;
    protected FrameDecodec              frameDecodec;
    protected DataHandler[]             handlers;
    protected long                      readTimeout = 3000;
    // 默认的超时等待时间是30分钟
    protected long                      waitTimeout = 1000 * 60 * 30;
    protected Object[]                  resultArray;
    protected int                       resultArrayLengthMask;
    protected final static int          base;
    protected final static int          scale;
    protected String                    remoteAddress;
    protected String                    localAddress;
    protected static final Unsafe       unsafe      = ReflectUtil.getUnsafe();
    static
    {
        base = unsafe.arrayBaseOffset(Object[].class);
        if (4 == unsafe.arrayIndexScale(Object[].class))
        {
            scale = 2;
        }
        else if (8 == unsafe.arrayIndexScale(Object[].class))
        {
            scale = 3;
        }
        else
        {
            throw new RuntimeException("错误的长度信息");
        }
    }
    
    @Override
    public void setHandlers(DataHandler... handlers)
    {
        this.handlers = handlers;
    }
    
    @Override
    public DataHandler[] getHandlers()
    {
        return handlers;
    }
    
    @Override
    public void setFrameDecodec(FrameDecodec frameDecodec)
    {
        this.frameDecodec = frameDecodec;
    }
    
    @Override
    public FrameDecodec getFrameDecodec()
    {
        return frameDecodec;
    }
    
    @Override
    public void setChannel(AsynchronousSocketChannel socketChannel)
    {
        this.socketChannel = socketChannel;
    }
    
    @Override
    public AsynchronousSocketChannel getSocketChannel()
    {
        return socketChannel;
    }
    
    @Override
    public boolean isOpen()
    {
        return openState.get() == OPEN;
    }
    
    @Override
    public void closeChannel()
    {
        if (openState.get() == CLOSE)
        {
            return;
        }
        if (openState.compareAndSet(OPEN, CLOSE))
        {
            try
            {
                socketChannel.close();
            }
            catch (IOException e)
            {
            }
        }
    }
    
    @Override
    public void setDataArrayLength(int resultArrayLength)
    {
        Verify.True(resultArrayLength > 1, "数组的大小必须大于1");
        Verify.True(Integer.bitCount(resultArrayLength) == 1, "数组的大小必须是2的次方幂");
        resultArray = new Object[resultArrayLength];
        resultArrayLengthMask = resultArrayLength - 1;
    }
    
    @Override
    public Object getData(long cursor)
    {
        return unsafe.getObject(resultArray, base + ((cursor & resultArrayLengthMask) << scale));
    }
    
    @Override
    public Object getDataVolatile(long cursor)
    {
        return unsafe.getObjectVolatile(resultArray, base + ((cursor & resultArrayLengthMask) << scale));
    }
    
    @Override
    public void putDataVolatile(Object obj, long cursor)
    {
        unsafe.putObjectVolatile(resultArray, base + ((cursor & resultArrayLengthMask) << scale), obj);
    }
    
    @Override
    public void setReadTimeout(long readTimeout)
    {
        this.readTimeout = readTimeout;
    }
    
    @Override
    public void setWaitTimeout(long waitTimeout)
    {
        this.waitTimeout = waitTimeout;
    }
    
    @Override
    public long getReadTimeout()
    {
        return readTimeout;
    }
    
    @Override
    public long getWaitTimeout()
    {
        return waitTimeout;
    }
    
    @Override
    public int getDataArraySize()
    {
        return resultArray.length;
    }
    
    @Override
    public String getLocalAddress()
    {
        if (localAddress == null)
        {
            try
            {
                localAddress = socketChannel.getLocalAddress().toString();
            }
            catch (IOException e)
            {
            }
        }
        return localAddress;
    }
    
    @Override
    public String getRemoteAddress()
    {
        if (remoteAddress == null)
        {
            try
            {
                remoteAddress = socketChannel.getRemoteAddress().toString();
            }
            catch (IOException e)
            {
            }
        }
        return remoteAddress;
    }
    
    public Object[] getDataArray()
    {
        return resultArray;
    }
}
