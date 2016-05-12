package com.jfireframework.jnet.server.server;

import com.jfireframework.baseutil.disruptor.Disruptor;
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
    private WaitStrategy        waitStrategy      = new BlockWaitStrategy();
    private int                 ringArraySize     = 1048576;                                                                                                 // 2的20次方
    private int                 socketThreadSize  = Runtime.getRuntime().availableProcessors() / 2 == 0 ? 1 : Runtime.getRuntime().availableProcessors() / 2;
    private int                 handlerThreadSize = Runtime.getRuntime().availableProcessors() / 2 == 0 ? 1 : Runtime.getRuntime().availableProcessors() / 2;
    private int                 ringArrayType     = Disruptor.SimpleMult;
    private WorkMode            workMode          = WorkMode.SYNC;
    
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
    
    public int getRingArraySize()
    {
        return ringArraySize;
    }
    
    public ServerConfig setRingArraySize(int ringArraySize)
    {
        this.ringArraySize = ringArraySize;
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
    
    public int getHandlerThreadSize()
    {
        return handlerThreadSize;
    }
    
    public ServerConfig setHandlerThreadSize(int handlerThreadSize)
    {
        this.handlerThreadSize = handlerThreadSize;
        return this;
    }
    
    public int getRingArrayType()
    {
        return ringArrayType;
    }
    
    public ServerConfig setRingArrayType(int ringArrayTpe)
    {
        this.ringArrayType = ringArrayTpe;
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
    
}
