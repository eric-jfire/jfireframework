package com.jfireframework.jnet2.server.CompletionHandler.weapon.capacity.sync;

import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ClosedChannelException;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.jnet2.common.channel.ChannelInitListener;
import com.jfireframework.jnet2.common.channel.impl.ServerChannel;
import com.jfireframework.jnet2.server.AioServer;
import com.jfireframework.jnet2.server.CompletionHandler.AcceptHandler;
import com.jfireframework.jnet2.server.CompletionHandler.weapon.capacity.sync.read.withoutpush.CapacityReadHandlerImpl;
import com.jfireframework.jnet2.server.util.PushMode;
import com.jfireframework.jnet2.server.util.ServerConfig;
import com.jfireframework.jnet2.server.util.WorkMode;

public class CapacityAcceptHandler implements AcceptHandler
{
    private final AsynchronousServerSocketChannel serverSocketChannel;
    private Logger                                logger = ConsoleLogFactory.getLogger();
    private ChannelInitListener                   initListener;
    private final int                             capacity;
    private final WorkMode                        workMode;
    private final PushMode                        pushMode;
    
    public CapacityAcceptHandler(AioServer aioServer, ServerConfig serverConfig)
    {
        pushMode = serverConfig.getPushMode();
        workMode = serverConfig.getWorkMode();
        capacity = serverConfig.getChannelCapacity();
        this.initListener = serverConfig.getInitListener();
        Verify.notNull(initListener, "initListener不能为空");
        serverSocketChannel = aioServer.getServerSocketChannel();
        initListener = serverConfig.getInitListener();
    }
    
    public void stop()
    {
    }
    
    @Override
    public void completed(AsynchronousSocketChannel socketChannel, Object attachment)
    {
        try
        {
            ServerChannel channelInfo = new ServerChannel(socketChannel);
            initListener.channelInit(channelInfo);
            CapacityReadHandler readHandler = null;
            if (workMode == WorkMode.ASYNC)
            {
                if (pushMode == PushMode.OFF)
                {
                    throw new UnsupportedOperationException();
                }
                else
                {
                    throw new UnsupportedOperationException();
                }
            }
            else
            {
                if (pushMode == PushMode.OFF)
                {
                    readHandler = new CapacityReadHandlerImpl(channelInfo, capacity);
                }
                else
                {
                    throw new UnsupportedOperationException();
                }
            }
            readHandler.readAndWait();
            serverSocketChannel.accept(null, this);
        }
        catch (Exception e)
        {
            logger.error("注册异常", e);
        }
    }
    
    @Override
    public void failed(Throwable exc, Object attachment)
    {
        if (exc instanceof AsynchronousCloseException)
        {
            logger.info("服务端监听链接被关闭");
        }
        else if (exc instanceof ClosedChannelException)
        {
            logger.info("服务端监听链接被关闭");
        }
        else
        {
            logger.error("链接异常关闭", exc);
        }
        Thread.currentThread().interrupt();
    }
}
