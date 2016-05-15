package com.jfireframework.jnet.server.CompletionHandler;

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
import com.jfireframework.jnet.server.util.AsyncTaskCenter;
import com.jfireframework.jnet.server.util.ServerConfig;
import com.jfireframework.jnet.server.util.WorkMode;
import com.jfireframework.jnet.server.util.WriteMode;

public class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, Object>
{
    private AioServer             aioServer;
    private Logger                logger = ConsoleLogFactory.getLogger();
    private ChannelInitListener   initListener;
    private final WorkMode        workMode;
    private final WriteMode       writeMode;
    private final AsyncTaskCenter asyncTaskCenter;
    private final int             maxBatchWriteNum;
    
    public AcceptHandler(AioServer aioServer, ServerConfig serverConfig)
    {
        this.initListener = serverConfig.getInitListener();
        Verify.notNull(initListener, "initListener不能为空");
        this.aioServer = aioServer;
        maxBatchWriteNum = serverConfig.getMaxBatchWriteNum();
        writeMode = serverConfig.getWriteMode();
        workMode = serverConfig.getWorkMode();
        switch (workMode)
        {
            case SYNC_WITH_ORDER:
                asyncTaskCenter = null;
                break;
            case ASYNC_WITH_ORDER:
                asyncTaskCenter = new AsyncTaskCenter(serverConfig.getAsyncThreadSize(), workMode);
                break;
            case MIX_WITH_ORDER:
                asyncTaskCenter = new AsyncTaskCenter(serverConfig.getAsyncThreadSize(), workMode);
                break;
            case ASYNC_WITHOUT_ORDER:
                asyncTaskCenter = new AsyncTaskCenter(serverConfig.getAsyncThreadSize(), workMode);
                break;
            default:
                asyncTaskCenter = null;
                break;
        }
    }
    
    public void stop()
    {
        if (asyncTaskCenter != null)
        {
            asyncTaskCenter.stop();
        }
    }
    
    @Override
    public void completed(AsynchronousSocketChannel socketChannel, Object attachment)
    {
        try
        {
            ServerChannel channelInfo = new ServerChannel();
            channelInfo.setChannel(socketChannel);
            initListener.channelInit(channelInfo);
            Verify.notNull(channelInfo.getDataArray(), "没有设置entryArraySize");
            Verify.notNull(channelInfo.getFrameDecodec(), "没有设置framedecodec");
            Verify.notNull(channelInfo.getHandlers(), "没有设置Datahandler");
            ReadCompletionHandler readCompletionHandler = new ReadCompletionHandler(channelInfo, workMode, asyncTaskCenter, writeMode, maxBatchWriteNum);
            readCompletionHandler.readAndWait();
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
