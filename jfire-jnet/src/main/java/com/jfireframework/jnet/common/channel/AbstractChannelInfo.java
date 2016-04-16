package com.jfireframework.jnet.common.channel;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.jnet.common.decodec.FrameDecodec;
import com.jfireframework.jnet.common.handler.DataHandler;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public abstract class AbstractChannelInfo implements ChannelInfo
{
    protected static final Unsafe       unsafe      = ReflectUtil.getUnsafe();
    public final static int             OPEN        = 1;
    public final static int             CLOSE       = 2;
    // 消息通道的打开状态
    protected volatile int              openState   = OPEN;
    protected static final long         _openState  = ReflectUtil.getFieldOffset("openState", AbstractChannelInfo.class);
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
    
    public void setHandlers(DataHandler... handlers)
    {
        this.handlers = handlers;
    }
    
    public DataHandler[] getHandlers()
    {
        return handlers;
    }
    
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
    public AsynchronousSocketChannel getChannel()
    {
        return socketChannel;
    }
    
    public AsynchronousSocketChannel socketChannel()
    {
        return socketChannel;
    }
    
    public boolean isOpen()
    {
        return openState == OPEN;
    }
    
    public void closeChannel()
    {
        if (openState == CLOSE)
        {
            return;
        }
        if (unsafe.compareAndSwapInt(this, _openState, OPEN, CLOSE))
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
    
    public void setResultArrayLength(int resultArrayLength)
    {
        Verify.True(resultArrayLength > 1, "数组的大小必须大于1");
        Verify.True(Integer.bitCount(resultArrayLength) == 1, "数组的大小必须是2的次方幂");
        resultArray = new Object[resultArrayLength];
        resultArrayLengthMask = resultArrayLength - 1;
    }
    
    public Object getResult(long cursor)
    {
        return unsafe.getObjectVolatile(resultArray, base + ((cursor & resultArrayLengthMask) << scale));
    }
    
    public void putResult(Object obj, long cursor)
    {
        unsafe.putObjectVolatile(resultArray, base + ((cursor & resultArrayLengthMask) << scale), obj);
    }
    
    public void setReadTimeout(long readTimeout)
    {
        this.readTimeout = readTimeout;
    }
    
    public void setWaitTimeout(long waitTimeout)
    {
        this.waitTimeout = waitTimeout;
    }
    
    public long getReadTimeout()
    {
        return readTimeout;
    }
    
    public long getWaitTimeout()
    {
        return waitTimeout;
    }
    
    public int getEntryArraySize()
    {
        return resultArray.length;
    }
    
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
    
    public Object[] getResultArray()
    {
        return resultArray;
    }
}
