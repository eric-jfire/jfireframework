package com.jfireframework.eventbus.bus;

import com.jfireframework.eventbus.event.ApplicationEvent;
import com.jfireframework.eventbus.event.Event;
import com.jfireframework.eventbus.handler.EventHandler;

/**
 * Created by linbin on 2016/9/19.
 */
public interface EventBus
{
    public <T> void addHandler(EventHandler<T> eventHandler);
    
    public void start();
    
    public void stop();
    
    public ApplicationEvent post(ApplicationEvent event);
    
    public ApplicationEvent post(Object data, Enum<? extends Event<?>> event);
}
