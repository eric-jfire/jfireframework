package com.jfireframework.jnet2.common.channel;

import java.nio.channels.AsynchronousSocketChannel;
import com.jfireframework.jnet2.common.decodec.FrameDecodec;
import com.jfireframework.jnet2.common.handler.DataHandler;

public interface JnetChannel
{
    
    public void setFrameDecodec(FrameDecodec frameDecodec);
    
    public void setHandlers(DataHandler... handlers);
    
    public FrameDecodec getFrameDecodec();
    
    public DataHandler[] getHandlers();
    
    public AsynchronousSocketChannel getSocketChannel();
    
    /**
     * 注意，方法的内部实现保证close方法实际上只会被调用一次。返回true意味着真正的调用了close方法。返回false，就意味着有别人已经调用了close方法
     */
    public boolean closeChannel();
    
    public boolean isOpen();
    
    public void setReadTimeout(long readTimeout);
    
    public void setWaitTimeout(long waitTimeout);
    
    public long getReadTimeout();
    
    public long getWaitTimeout();
    
    public String getLocalAddress();
    
    public String getRemoteAddress();
}
