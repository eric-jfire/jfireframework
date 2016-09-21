package com.jfireframework.eventbus.handler;

import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.event.ApplicationEvent;
import com.jfireframework.eventbus.event.Event;

public interface EventHandlerContext<T>
{
    public Enum<? extends Event<T>> interest();
    
    public void addHandler(EventHandler<T> eventHandler);
    
    public void endAdd();
    
    public void handle(ApplicationEvent applicationEvent, EventBus eventBus);
}
