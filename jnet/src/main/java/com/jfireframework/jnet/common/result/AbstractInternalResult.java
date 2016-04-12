package com.jfireframework.jnet.common.result;

public abstract class AbstractInternalResult implements InternalResult
{
	protected Object	data;
	protected int		index;
	
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
	
}
