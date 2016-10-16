package com.jfireframework.context.event;

import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.eventcontext.EventContext;

public interface EventPoster
{
    public EventContext post(EventContext event);
    
    public EventContext post(Object data, Enum<? extends EventConfig<?>> event, Object rowkey);
    
    public EventContext post(Object data, Enum<? extends EventConfig<?>> event);
}
