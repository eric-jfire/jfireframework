package com.jfireframework.jnet.common.channel.impl;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.resource.ResourceCloseAgent;
import com.jfireframework.jnet.common.channel.JnetChannel;
import com.jfireframework.jnet.common.decodec.FrameDecodec;
import com.jfireframework.jnet.common.handler.DataHandler;
import com.jfireframework.jnet.common.util.ChannelCloseCallback;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public abstract class AbstractChannel implements JnetChannel
{
    protected final AsynchronousSocketChannel                     socketChannel;
    protected FrameDecodec                                        frameDecodec;
    protected DataHandler[]                                       handlers;
    protected long                                                readTimeout = 3000;
    // 默认的超时等待时间是30分钟
    protected long                                                waitTimeout = 1000 * 60 * 30;
    protected String                                              remoteAddress;
    protected String                                              localAddress;
    protected static final Unsafe                                 unsafe      = ReflectUtil.getUnsafe();
    protected final ResourceCloseAgent<AsynchronousSocketChannel> openState;
    
    public AbstractChannel(AsynchronousSocketChannel socketChannel)
    {
        this.socketChannel = socketChannel;
        openState = new ResourceCloseAgent<AsynchronousSocketChannel>(socketChannel, ChannelCloseCallback.instance);
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
    public AsynchronousSocketChannel getSocketChannel()
    {
        return socketChannel;
    }
    
    @Override
    public boolean isOpen()
    {
        return openState.isOpen();
    }
    
    @Override
    public boolean closeChannel()
    {
        return openState.close();
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
    
}
