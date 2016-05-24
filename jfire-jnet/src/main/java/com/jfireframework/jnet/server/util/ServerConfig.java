package com.jfireframework.jnet.server.util;

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
    private DisruptorWaitMode   waitMode         = DisruptorWaitMode.PARK;
    private int                 socketThreadSize = Runtime.getRuntime().availableProcessors() / 2 == 0 ? 1 : Runtime.getRuntime().availableProcessors() / 2;
    private int                 asyncThreadSize  = Runtime.getRuntime().availableProcessors() / 2 == 0 ? 1 : Runtime.getRuntime().availableProcessors() / 2;
    private WorkMode            workMode         = WorkMode.SYNC_WITH_ORDER;
    private WriteMode           writeMode        = WriteMode.BATCH_WRITE;
    private int                 maxBatchWriteNum = 10;
    private int                 channelCapacity  = 16;
    private int                 asyncCapacity    = 1024;
    
    public int getAsyncCapacity()
    {
        return asyncCapacity;
    }
    
    public ServerConfig setAsyncCapacity(int asyncCapacity)
    {
        this.asyncCapacity = asyncCapacity;
        return this;
    }
    
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
    
    public DisruptorWaitMode getWaitMode()
    {
        return waitMode;
    }
    
    public ServerConfig setWaitMode(DisruptorWaitMode waitMode)
    {
        this.waitMode = waitMode;
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
    
    public int getMaxBatchWriteNum()
    {
        return maxBatchWriteNum;
    }
    
    public ServerConfig setMaxBatchWriteNum(int maxBatchWriteNum)
    {
        this.maxBatchWriteNum = maxBatchWriteNum;
        return this;
    }
    
    public int getChannelCapacity()
    {
        return channelCapacity;
    }
    
    public ServerConfig setChannelCapacity(int channelCapacity)
    {
        this.channelCapacity = channelCapacity;
        return this;
    }
    
}
