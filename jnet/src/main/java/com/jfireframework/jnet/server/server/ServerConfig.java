package com.jfireframework.jnet.server.server;

import com.jfireframework.baseutil.disruptor.Disruptor;
import com.jfireframework.baseutil.disruptor.waitstrategy.BlockWaitStrategy;
import com.jfireframework.baseutil.disruptor.waitstrategy.WaitStrategy;
import com.jfireframework.jnet.common.decodec.FrameDecodec;

/**
 * 服务器的配置类，用以配置初始化服务器的信息。
 * 其中默认读取超时为3秒，空闲等待为30秒
 * 
 * @author 林斌（windfire@zailanghua.com）
 *         
 */
public class ServerConfig
{
    private ChannelInitListener initListener;
    private FrameDecodec        frameDecodec;
    // 链接空闲时的等待时间，默认为30分钟
    private long                waitTimeout       = 1000 * 60 * 30;
    // 读取超时时间，默认为3秒
    private long                readTiemout       = 10000;
    // 服务器的启动端口
    private int                 port;
    private WaitStrategy        waitStrategy      = new BlockWaitStrategy();
    private int                 ringArraySize     = 1048576;                                                                                                 // 2的20次方
    private int                 socketThreadSize  = Runtime.getRuntime().availableProcessors() / 2 == 0 ? 1 : Runtime.getRuntime().availableProcessors() / 2;
    private int                 handlerThreadSize = Runtime.getRuntime().availableProcessors() / 2 == 0 ? 1 : Runtime.getRuntime().availableProcessors() / 2;
    private int                 ringArrayType     = Disruptor.SimpleMult;
                                                  
    public ChannelInitListener getInitListener()
    {
        return initListener;
    }
    
    public void setInitListener(ChannelInitListener initListener)
    {
        this.initListener = initListener;
    }
    
    public FrameDecodec getFrameDecodec()
    {
        return frameDecodec;
    }
    
    public void setFrameDecodec(FrameDecodec frameDecodec)
    {
        this.frameDecodec = frameDecodec;
    }
    
    public long getWaitTimeout()
    {
        return waitTimeout;
    }
    
    public ServerConfig setWaitTimeout(long waitTimeout)
    {
        this.waitTimeout = waitTimeout;
        return this;
    }
    
    public long getReadTiemout()
    {
        return readTiemout;
    }
    
    public ServerConfig setReadTiemout(long readTiemout)
    {
        this.readTiemout = readTiemout;
        return this;
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
    
}
