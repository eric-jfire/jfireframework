package com.jfireframework.jnet.common.result;

public interface InternalResult
{
	
	/**
	 * 获取当前中间结果的暂存数据。
	 * 注意：框架只会在数据流转的开始和结束将DataHandler的返回结果放入InternalResult中。在流转的中间过程中，
	 * 里面的内容是不会更新。
	 * 
	 * @return
	 */
	public Object getData();
	
	/**
	 * 设置中间结果的暂存数据
	 * 
	 * @param data
	 */
	public void setData(Object data);
	
	/**
	 * 获取当前正在处理的处理器序号
	 * 
	 * @return
	 */
	public int getIndex();
	
	/**
	 * 设置将要处理的处理器序号
	 * 
	 * @param index
	 */
	public void setIndex(int index);
	
	/**
	 * 关闭当前通道
	 */
	public void closeChannel();
	
}
