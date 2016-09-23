package com.jfireframework.context.event;

import com.jfireframework.eventbus.event.Event;
import com.jfireframework.eventbus.event.EventContext;

public interface EventPoster
{
    public EventContext post(EventContext event);
    
    public EventContext post(Object data, Enum<? extends Event<?>> event, Object rowkey);
    
    public EventContext post(Object data, Enum<? extends Event<?>> event);
}
