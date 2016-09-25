package com.jfireframework.eventbus.handler;

import com.jfireframework.baseutil.order.Order;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.event.Event;
import com.jfireframework.eventbus.eventcontext.EventContext;

public interface EventHandler<T> extends Order
{
    /**
     * 处理一个事件
     * 
     * @param event
     */
    public void handle(EventContext eventContext, EventBus eventBus);
    
    public Enum<? extends Event<T>> interest();
    
}
