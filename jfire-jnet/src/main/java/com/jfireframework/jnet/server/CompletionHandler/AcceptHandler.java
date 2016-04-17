package com.jfireframework.jnet.server.CompletionHandler;

import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.CompletionHandler;
import com.jfireframework.baseutil.disruptor.Disruptor;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.jnet.common.channel.ChannelInitListener;
import com.jfireframework.jnet.common.channel.ServerChannelInfo;
import com.jfireframework.jnet.server.server.AioServer;
import com.jfireframework.jnet.server.server.ServerConfig;

public class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, Object>
{
    private AioServer           aioServer;
    private Logger              logger = ConsoleLogFactory.getLogger();
    private ChannelInitListener initListener;
    private Disruptor           disruptor;
    
    public AcceptHandler(AioServer aioServer, ServerConfig serverConfig)
    {
        this.initListener = serverConfig.getInitListener();
        Verify.notNull(initListener, "initListener不能为空");
        this.aioServer = aioServer;
        ServerInternalResultAction[] actions = new ServerInternalResultAction[serverConfig.getHandlerThreadSize()];
        for (int i = 0; i < actions.length; i++)
        {
            actions[i] = new ServerInternalResultAction();
        }
        disruptor = new Disruptor(serverConfig.getRingArraySize(), serverConfig.getWaitStrategy(), actions, serverConfig.getRingArrayType(), serverConfig.getHandlerThreadSize());
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
            ServerChannelInfo channelInfo = new ServerChannelInfo();
            channelInfo.setChannel(socketChannel);
            initListener.channelInit(channelInfo);
            Verify.notNull(channelInfo.getResultArray(), "没有设置entryArraySize");
            Verify.notNull(channelInfo.getFrameDecodec(), "没有设置framedecodec");
            Verify.notNull(channelInfo.getHandlers(), "没有设置Datahandler");
            ReadCompletionHandler readCompletionHandler = new ReadCompletionHandler(channelInfo, disruptor);
//            logger.debug("开启一个新通道{}", channelInfo.getRemoteAddress());
            readCompletionHandler.startReadWait();
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
