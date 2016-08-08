package com.jfireframework.jnet2.server.util;

import com.jfireframework.jnet2.common.channel.ChannelInitListener;

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
    /**
     * 处理socket事件的起始线程数。如果线程池模式选择固定线程数模式的话，则这个数值就是线程数的值。如果线程池模式选择cache模式的话，则这个数值是初始线程数。
     */
    private int                 socketThreadSize = Runtime.getRuntime().availableProcessors();
    /**
     * 异步处理线程数。这个数字是disruptor的处理线程数。如果异步任务执行时间较长，适当增大该数字可以得到吞吐量的提升。
     */
    private int                 asyncThreadSize  = Runtime.getRuntime().availableProcessors();
    private WorkMode            workMode         = WorkMode.SYNC;
    private WriteMode           writeMode        = WriteMode.BATCH_WRITE;
    private ExecutorMode        executorMode     = ExecutorMode.CACHED;
    private AcceptMode          acceptMode       = AcceptMode.weapon_capacity;
    private PushMode            pushMode         = PushMode.OFF;
    private int                 maxBatchWriteNum = 10;
    private int                 channelCapacity  = 16;
    private int                 asyncCapacity    = 1024;
    
    public PushMode getPushMode()
    {
        return pushMode;
    }
    
    public void setPushMode(PushMode pushMode)
    {
        this.pushMode = pushMode;
    }
    
    public AcceptMode getAcceptMode()
    {
        return acceptMode;
    }
    
    public void setAcceptMode(AcceptMode acceptMode)
    {
        this.acceptMode = acceptMode;
    }
    
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
    
    public ExecutorMode getExecutorMode()
    {
        return executorMode;
    }
    
    public void setExecutorMode(ExecutorMode executorMode)
    {
        this.executorMode = executorMode;
    }
    
}
