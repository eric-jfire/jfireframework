package com.jfireframework.jnet.server.CompletionHandler.x.impl;

import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.CompletionHandler;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.jnet.common.channel.ChannelInitListener;
import com.jfireframework.jnet.common.channel.impl.ServerChannel;
import com.jfireframework.jnet.server.AioServer;
import com.jfireframework.jnet.server.CompletionHandler.x.WeaponReadHandler;
import com.jfireframework.jnet.server.util.ServerConfig;

public class WeaponAcceptHandler implements CompletionHandler<AsynchronousSocketChannel, Object>
{
    private AioServer           aioServer;
    private Logger              logger = ConsoleLogFactory.getLogger();
    private ChannelInitListener initListener;
    private final int           capacity;
    
    public WeaponAcceptHandler(AioServer aioServer, ServerConfig serverConfig)
    {
        capacity = serverConfig.getChannelCapacity();
        this.initListener = serverConfig.getInitListener();
        Verify.notNull(initListener, "initListener不能为空");
        this.aioServer = aioServer;
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
            ServerChannel channelInfo = new ServerChannel();
            channelInfo.setCapacity(capacity);
            channelInfo.setChannel(socketChannel);
            initListener.channelInit(channelInfo);
            WeaponReadHandler weaponReadHandler = new WeaponSyncReadHandlerImpl(channelInfo);
            weaponReadHandler.notifyRead();
            aioServer.getServerSocketChannel().accept(null, this);
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
