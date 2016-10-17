package com.jfireframework.eventbus.handler;

import com.jfireframework.baseutil.order.Order;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.event.EventConfig;

public interface EventHandler<T extends Enum<? extends EventConfig>, E> extends Order
{
    /**
     * 处理一个事件
     * 
     * @param event
     */
    public Object handle(E data, EventBus eventBus);
    
    public T interest();
    
}
