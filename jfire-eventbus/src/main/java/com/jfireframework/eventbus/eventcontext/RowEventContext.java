package com.jfireframework.eventbus.eventcontext;

import com.jfireframework.eventbus.event.EventConfig;

public interface RowEventContext<T extends Enum<? extends EventConfig>> extends EventContext<T>
{
    /**
     * 返回一个用于标识本行数据的key
     * 
     * @return
     */
    public Object rowkey();
}
