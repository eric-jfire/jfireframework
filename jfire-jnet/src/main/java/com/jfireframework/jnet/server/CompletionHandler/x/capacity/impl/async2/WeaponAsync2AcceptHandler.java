package com.jfireframework.jnet.server.CompletionHandler.x.capacity.impl.async2;

import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ClosedChannelException;
import com.jfireframework.baseutil.disruptor.Disruptor;
import com.jfireframework.baseutil.disruptor.EntryAction;
import com.jfireframework.baseutil.disruptor.waitstrategy.ParkWaitStrategy;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.jnet.common.channel.ChannelInitListener;
import com.jfireframework.jnet.common.channel.impl.ServerChannel;
import com.jfireframework.jnet.server.AioServer;
import com.jfireframework.jnet.server.CompletionHandler.AcceptHandler;
import com.jfireframework.jnet.server.util.ServerConfig;

public class WeaponAsync2AcceptHandler implements AcceptHandler
{
    private AioServer           aioServer;
    private Logger              logger = ConsoleLogFactory.getLogger();
    private ChannelInitListener initListener;
    private final int           capacity;
    private final Disruptor     disruptor;
    
    public WeaponAsync2AcceptHandler(AioServer aioServer, ServerConfig serverConfig)
    {
        capacity = serverConfig.getChannelCapacity();
        this.initListener = serverConfig.getInitListener();
        Verify.notNull(initListener, "initListener不能为空");
        this.aioServer = aioServer;
        initListener = serverConfig.getInitListener();
        EntryAction[] actions = new EntryAction[serverConfig.getAsyncThreadSize()];
        for (int i = 0; i < actions.length; i++)
        {
            actions[i] = new Async2TaskAction();
        }
        Thread[] threads = new Thread[actions.length];
        for (int i = 0; i < threads.length; i++)
        {
            threads[i] = new Thread(actions[i], "disruptor-" + i);
        }
        disruptor = new Disruptor(serverConfig.getAsyncCapacity(), actions, threads, new ParkWaitStrategy(threads));
    }
    
    public void stop()
    {
        disruptor.stop();
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
            WeaponAsync2ReadHandler readHandler = new WeaponAsync2ReadHandlerImpl(channelInfo, disruptor);
            readHandler.readAndWait(true);
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
