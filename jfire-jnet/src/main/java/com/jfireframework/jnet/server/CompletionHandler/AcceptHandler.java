package com.jfireframework.jnet.server.CompletionHandler;

import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.CompletionHandler;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.jnet.common.channel.ChannelInitListener;
import com.jfireframework.jnet.common.channel.ServerChannelInfo;
import com.jfireframework.jnet.server.CompletionHandler.async.AsyncReadCompletionHandler;
import com.jfireframework.jnet.server.CompletionHandler.async.AsyncServerInternalResultAction;
import com.jfireframework.jnet.server.server.AioServer;
import com.jfireframework.jnet.server.server.ServerConfig;
import com.jfireframework.jnet.server.server.WorkMode;
import com.jfireframework.jnet.util.QueuedResultCenter;

public class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, Object>
{
    private AioServer                aioServer;
    private Logger                   logger = ConsoleLogFactory.getLogger();
    private ChannelInitListener      initListener;
    private final WorkMode           workMode;
    private final QueuedResultCenter queuedResultCenter;
    
    public AcceptHandler(AioServer aioServer, ServerConfig serverConfig)
    {
        this.initListener = serverConfig.getInitListener();
        Verify.notNull(initListener, "initListener不能为空");
        this.aioServer = aioServer;
        workMode = serverConfig.getWorkMode();
        if (workMode == WorkMode.ASYNC_WITHOUT_ORDER)
        {
            AsyncServerInternalResultAction[] actions = new AsyncServerInternalResultAction[serverConfig.getAsyncThreadSize()];
            for (int i = 0; i < actions.length; i++)
            {
                actions[i] = new AsyncServerInternalResultAction();
            }
            queuedResultCenter = null;
        }
        else
        {
            queuedResultCenter = new QueuedResultCenter(serverConfig.getAsyncThreadSize());
        }
    }
    
    public void stop()
    {
        queuedResultCenter.stop();
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
            if (workMode == WorkMode.ASYNC_WITHOUT_ORDER)
            {
                AsyncReadCompletionHandler readCompletionHandler = new AsyncReadCompletionHandler(channelInfo, null);
                readCompletionHandler.readAndWait();
            }
            else
            {
                ReadCompletionHandler readCompletionHandler = new ReadCompletionHandler(channelInfo, workMode, queuedResultCenter.getResults());
                readCompletionHandler.readAndWait();
            }
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
