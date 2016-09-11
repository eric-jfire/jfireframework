package com.jfireframework.eventbus.handler;

import com.jfireframework.baseutil.order.Order;
import com.jfireframework.eventbus.event.ApplicationEvent;
import com.jfireframework.eventbus.event.Event;

public interface EventHandler<T> extends Order
{
    /**
     * 处理一个事件
     * 
     * @param event
     */
    public void handle(ApplicationEvent event);
    
    public Enum<? extends Event<T>> interest();
    
}
