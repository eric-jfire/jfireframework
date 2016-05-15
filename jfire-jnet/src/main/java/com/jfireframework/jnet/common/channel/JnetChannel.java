package com.jfireframework.jnet.common.channel;

import java.nio.channels.AsynchronousSocketChannel;
import com.jfireframework.jnet.common.decodec.FrameDecodec;
import com.jfireframework.jnet.common.handler.DataHandler;

public interface JnetChannel
{
    public void setFrameDecodec(FrameDecodec frameDecodec);
    
    public void setHandlers(DataHandler... handlers);
    
    public FrameDecodec getFrameDecodec();
    
    public DataHandler[] getHandlers();
    
    public void setChannel(AsynchronousSocketChannel socketChannel);
    
    public AsynchronousSocketChannel getSocketChannel();
    
    /**
     * 注意，方法的内部实现需要保证通道实际上只会被关闭一次，也就是通过一个volatile的标志位，cas的关闭
     */
    public void closeChannel();
    
    public boolean isOpen();
    
    public void setDataArrayLength(int size);
    
    public Object[] getDataArray();
    
    public int getDataArraySize();
    
    public Object getData(long cursor);
    
    public Object getDataVolatile(long cursor);
    
    public void putDataVolatile(Object obj, long cursor);
    
    public void setReadTimeout(long readTimeout);
    
    public void setWaitTimeout(long waitTimeout);
    
    public long getReadTimeout();
    
    public long getWaitTimeout();
    
    public String getLocalAddress();
    
    public String getRemoteAddress();
}
