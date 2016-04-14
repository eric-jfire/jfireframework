package com.jfireframework.jnet.common.result;

import com.jfireframework.jnet.common.channel.ChannelInfo;

public abstract class AbstractInternalResult implements InternalResult
{
	protected Object		data;
	protected int			index;
	protected ChannelInfo	channelInfo;
	
	public Object getData()
	{
		return data;
	}
	
	public void setData(Object data)
	{
		this.data = data;
	}
	
	public int getIndex()
	{
		return index;
	}
	
	public void setIndex(int index)
	{
		this.index = index;
	}
	
	public ChannelInfo getChannelInfo()
	{
		return channelInfo;
	}
	
	public void setChannelInfo(ChannelInfo channelInfo)
	{
		this.channelInfo = channelInfo;
	}
	
	@Override
	public void closeChannel()
	{
		channelInfo.closeChannel();
	}
	
}
