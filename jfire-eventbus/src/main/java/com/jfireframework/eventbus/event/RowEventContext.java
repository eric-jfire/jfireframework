package com.jfireframework.eventbus.event;

public interface RowEventContext extends EventContext
{
    /**
     * 返回一个用于标识本行数据的key
     * 
     * @return
     */
    public Object rowkey();
}
