package com.jfireframework.eventbus.bus;

import com.jfireframework.eventbus.event.Event;
import com.jfireframework.eventbus.event.EventContext;
import com.jfireframework.eventbus.handler.EventHandler;

public interface EventBus
{
    public <T> void addHandler(EventHandler<T> eventHandler);
    
    public void start();
    
    public void stop();
    
    public EventContext post(EventContext event);
    
    public EventContext post(Object data, Enum<? extends Event<?>> event, Object rowkey);
    
    public EventContext post(Object data, Enum<? extends Event<?>> event);
}
