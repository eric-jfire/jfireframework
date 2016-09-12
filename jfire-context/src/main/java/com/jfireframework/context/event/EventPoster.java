package com.jfireframework.context.event;

import com.jfireframework.eventbus.event.ApplicationEvent;
import com.jfireframework.eventbus.event.Event;

public interface EventPoster
{
    public ApplicationEvent post(ApplicationEvent event);
    
    public ApplicationEvent post(Object data, Enum<? extends Event<?>> event);
}
