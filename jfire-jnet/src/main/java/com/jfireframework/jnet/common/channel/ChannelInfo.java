package com.jfireframework.jnet.common.channel;

import java.nio.channels.AsynchronousSocketChannel;
import com.jfireframework.jnet.common.decodec.FrameDecodec;
import com.jfireframework.jnet.common.handler.DataHandler;

public interface ChannelInfo
{
	public void setFrameDecodec(FrameDecodec frameDecodec);
	
	public void setHandlers(DataHandler... handlers);
	
	public FrameDecodec getFrameDecodec();
	
	public DataHandler[] getHandlers();
	
	public void setChannel(AsynchronousSocketChannel socketChannel);
	
	public AsynchronousSocketChannel getChannel();
	
	/**
	 * 注意，方法的内部实现需要保证通道实际上只会被关闭一次，也就是通过一个volatile的标志位，cas的关闭
	 */
	public void closeChannel();
	
	public AsynchronousSocketChannel socketChannel();
	
	public boolean isOpen();
	
	public void setEntryArraySize(int entrySize);
	
	public int getEntryArraySize();
	
	public Object getEntry(long cursor);
	
	public void putEntry(Object obj, long cursor);
	
	public void setReadTimeout(long readTimeout);
	
	public void setWaitTimeout(long waitTimeout);
	
	public long getReadTimeout();
	
	public long getWaitTimeout();
}
