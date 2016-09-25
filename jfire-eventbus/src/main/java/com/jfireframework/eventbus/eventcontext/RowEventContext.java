package com.jfireframework.eventbus.eventcontext;

public interface RowEventContext extends EventContext
{
    /**
     * 返回一个用于标识本行数据的key
     * 
     * @return
     */
    public Object rowkey();
}
