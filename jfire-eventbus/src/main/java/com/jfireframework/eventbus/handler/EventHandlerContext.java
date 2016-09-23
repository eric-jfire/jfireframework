package com.jfireframework.eventbus.handler;

import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.event.Event;
import com.jfireframework.eventbus.event.EventContext;

public interface EventHandlerContext<T>
{
    public Enum<? extends Event<T>> interest();
    
    public void addHandler(EventHandler<T> eventHandler);
    
    public void endAdd();
    
    public void handle(EventContext eventContext, EventBus eventBus);
}
