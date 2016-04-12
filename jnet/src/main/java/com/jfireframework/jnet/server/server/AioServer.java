package com.jfireframework.jnet.server.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.jnet.server.CompletionHandler.AcceptHandler;

public class AioServer
{
	private AcceptHandler					acceptCompleteHandler;
	private AsynchronousServerSocketChannel	serverSocketChannel;
	private Logger							logger	= ConsoleLogFactory.getLogger();
	private AsynchronousChannelGroup		channelGroup;
	private ServerConfig					serverConfig;
	
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
		acceptCompleteHandler = new AcceptHandler(this, serverConfig);
		try
		{
			channelGroup = AsynchronousChannelGroup.withFixedThreadPool(serverConfig.getSocketThreadSize(), new ThreadFactory() {
				private int i = 1;
				
				public Thread newThread(Runnable r)
				{
					return new Thread(r, "服务端通信处理线程-" + i++);
				}
			});
			serverSocketChannel = AsynchronousServerSocketChannel.open(channelGroup).bind(new InetSocketAddress(serverConfig.getPort()));
			logger.info("监听启动");
			serverSocketChannel.accept(null, acceptCompleteHandler);
		}
		catch (IOException e)
		{
			logger.error("服务器启动失败", e);
			throw new RuntimeException(e);
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
			acceptCompleteHandler.stop();
			logger.info("服务器关闭");
		}
		catch (Exception e)
		{
			logger.error("关闭服务器失败", e);
			throw new RuntimeException(e);
		}
	}
}
