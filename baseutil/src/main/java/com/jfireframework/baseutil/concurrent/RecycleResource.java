package com.jfireframework.baseutil.concurrent;

public interface RecycleResource
{
	
	/**
	 * 增加一个资源的持有数量，返回false表示当前资源处于不可以增加持有的状态
	 * 
	 * @return
	 */
	public void acquire();
	
	public void release();
}
