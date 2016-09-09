package com.jfireframework.eventbus.handler;

import com.jfireframework.eventbus.event.ApplicationEvent;
import com.jfireframework.eventbus.event.Event;

public interface EventHandlerContext<T>
{
    public Event<T> interest();
    
    public void addHandler(EventHandler<T> eventHandler);
    
    public void endAdd();
    
    public void handle(ApplicationEvent applicationEvent);
}
