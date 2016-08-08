package com.jfireframework.jnet.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import com.jfireframework.baseutil.collection.buffer.ByteBufPool;
import com.jfireframework.baseutil.collection.buffer.QueueFactory;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.jnet.server.CompletionHandler.AcceptHandler;
import com.jfireframework.jnet.server.CompletionHandler.weapon.capacity.sync.WeaponCapacityAcceptHandler;
import com.jfireframework.jnet.server.CompletionHandler.weapon.single.WeaponSingleAcceptHandler;
import com.jfireframework.jnet.server.util.ServerConfig;

public class AioServer
{
    private Lock                            lock     = new ReentrantLock();
    private Condition                       shutdown = lock.newCondition();
    private AcceptHandler                   acceptHandler;
    private AsynchronousServerSocketChannel serverSocketChannel;
    private Logger                          logger   = ConsoleLogFactory.getLogger();
    private AsynchronousChannelGroup        channelGroup;
    private ServerConfig                    serverConfig;
    static
    {
        class TransferQueueFactory implements QueueFactory
        {
            
            @Override
            public <T> Queue<T> newInstance()
            {
                return new java.util.concurrent.LinkedTransferQueue<T>();
            }
            
        }
        ByteBufPool.queueFactory = new TransferQueueFactory();
    }
    
    public AioServer(ServerConfig serverConfig)
    {
        this.serverConfig = serverConfig;
    }
    
    public AsynchronousServerSocketChannel getServerSocketChannel()
    {
        return serverSocketChannel;
    }
    
    /**
     * 以端口初始化server服务器。
     * 
     * @param port
     */
    public void start()
    {
        ThreadFactory threadFactory = new ThreadFactory() {
            int i = 1;
            
            @Override
            public Thread newThread(Runnable r)
            {
                return new Thread(r, "服务端socket线程-" + (i++));
            }
        };
        try
        {
            switch (serverConfig.getExecutorMode())
            {
                case FIX:
                    channelGroup = AsynchronousChannelGroup.withFixedThreadPool(serverConfig.getSocketThreadSize(), threadFactory);
                    break;
                case CACHED:
                    channelGroup = AsynchronousChannelGroup.withCachedThreadPool(Executors.newCachedThreadPool(threadFactory), serverConfig.getSocketThreadSize());
                    break;
            }
            serverSocketChannel = AsynchronousServerSocketChannel.open(channelGroup).bind(new InetSocketAddress(serverConfig.getPort()));
            logger.info("监听启动");
            switch (serverConfig.getAcceptMode())
            {
                case weapon_capacity:
                    acceptHandler = new WeaponCapacityAcceptHandler(this, serverConfig);
                    break;
                case weapon_single:
                    acceptHandler = new WeaponSingleAcceptHandler(this, serverConfig);
                    break;
            }
            serverSocketChannel.accept(null, acceptHandler);
        }
        catch (IOException e)
        {
            logger.error("服务器启动失败", e);
            throw new RuntimeException(e);
        }
    }
    
    public void waitForShutdown()
    {
        lock.lock();
        try
        {
            shutdown.await();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        finally
        {
            lock.unlock();
        }
    }
    
    public void stop()
    {
        try
        {
            if (channelGroup != null)
            {
                channelGroup.shutdownNow();
                channelGroup.awaitTermination(10, TimeUnit.SECONDS);
            }
            serverSocketChannel.close();
            acceptHandler.stop();
            logger.info("服务器关闭");
            lock.lock();
            try
            {
                shutdown.signal();
            }
            finally
            {
                lock.unlock();
            }
        }
        catch (Exception e)
        {
            logger.error("关闭服务器失败", e);
            throw new RuntimeException(e);
        }
    }
}
