package com.jfireframework.jnet.server.util;

import com.jfireframework.baseutil.disruptor.waitstrategy.BlockWaitStrategy;
import com.jfireframework.baseutil.disruptor.waitstrategy.WaitStrategy;
import com.jfireframework.jnet.common.channel.ChannelInitListener;

/**
 * 服务器的配置类，用以配置初始化服务器的信息。
 * 
 * @author 林斌（eric@jfire.cn）
 * 
 */
public class ServerConfig
{
    private ChannelInitListener initListener;
    // 服务器的启动端口
    private int                 port;
    private WaitStrategy        waitStrategy     = new BlockWaitStrategy();
    private int                 socketThreadSize = Runtime.getRuntime().availableProcessors() / 2 == 0 ? 1 : Runtime.getRuntime().availableProcessors() / 2;
    private int                 asyncThreadSize  = Runtime.getRuntime().availableProcessors() / 2 == 0 ? 1 : Runtime.getRuntime().availableProcessors() / 2;
    private WorkMode            workMode         = WorkMode.SYNC;
    private WriteMode           writeMode        = WriteMode.BATCH_WRITE;
    
    public WriteMode getWriteMode()
    {
        return writeMode;
    }
    
    public ServerConfig setWriteMode(WriteMode writeMode)
    {
        this.writeMode = writeMode;
        return this;
    }
    
    public ChannelInitListener getInitListener()
    {
        return initListener;
    }
    
    public void setInitListener(ChannelInitListener initListener)
    {
        this.initListener = initListener;
    }
    
    public int getPort()
    {
        return port;
    }
    
    public ServerConfig setPort(int port)
    {
        if (port < 0 || port > 63355)
        {
            throw new RuntimeException("设定的端口号异常，超出了系统范围");
        }
        this.port = port;
        return this;
    }
    
    public WaitStrategy getWaitStrategy()
    {
        return waitStrategy;
    }
    
    public ServerConfig setWaitStrategy(WaitStrategy waitStrategy)
    {
        this.waitStrategy = waitStrategy;
        return this;
    }
    
    public int getSocketThreadSize()
    {
        return socketThreadSize;
    }
    
    public ServerConfig setSocketThreadSize(int socketThreadSize)
    {
        this.socketThreadSize = socketThreadSize;
        
        return this;
    }
    
    public WorkMode getWorkMode()
    {
        return workMode;
    }
    
    public ServerConfig setWorkMode(WorkMode workMode)
    {
        this.workMode = workMode;
        return this;
    }
    
    public int getAsyncThreadSize()
    {
        return asyncThreadSize;
    }
    
    public ServerConfig setAsyncThreadSize(int asyncThreadSize)
    {
        this.asyncThreadSize = asyncThreadSize;
        return this;
    }
    
}
