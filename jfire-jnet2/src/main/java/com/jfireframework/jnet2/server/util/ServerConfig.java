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
    private int                 port            = -1;
    private DisruptorWaitMode   waitMode        = DisruptorWaitMode.PARK;
    /**
     * 处理socket事件的起始线程数。如果线程池模式选择固定线程数模式的话，则这个数值就是线程数的值。如果线程池模式选择cache模式的话，则这个数值是初始线程数。
     */
    private int                 socketThreadNum = Runtime.getRuntime().availableProcessors();
    /**
     * 异步处理线程数。这个数字是disruptor的处理线程数。如果异步任务执行时间较长，适当增大该数字可以得到吞吐量的提升。
     */
    private int                 asyncThreadNum  = Runtime.getRuntime().availableProcessors();
    private ExecutorMode        executorMode    = ExecutorMode.FIX;
    private WorkMode            workMode        = WorkMode.SYNC;
    private AcceptMode          acceptMode      = AcceptMode.SINGLE;
    private PushMode            pushMode        = PushMode.OFF;
    private int                 channelCapacity = 8;
    private int                 asyncCapacity   = 1024;
    private boolean             localTestMode   = false;
    
    public boolean isLocalTestMode()
    {
        return localTestMode;
    }
    
    public void setLocalTestMode(boolean localTestMode)
    {
        this.localTestMode = localTestMode;
    }
    
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
    
    public WorkMode getWorkMode()
    {
        return workMode;
    }
    
    public ServerConfig setWorkMode(WorkMode workMode)
    {
        this.workMode = workMode;
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
    
    public int getSocketThreadNum()
    {
        return socketThreadNum;
    }
    
    public ServerConfig setSocketThreadNum(int socketThreadNum)
    {
        this.socketThreadNum = socketThreadNum;
        return this;
    }
    
    public int getAsyncThreadNum()
    {
        return asyncThreadNum;
    }
    
    public ServerConfig setAsyncThreadNum(int asyncThreadNum)
    {
        this.asyncThreadNum = asyncThreadNum;
        return this;
    }
    
}
