package com.jfireframework.context.event;

import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.eventcontext.EventContext;

public interface EventPoster
{
    public <T> EventContext<T> post(Object data, Enum<? extends EventConfig> event, Object rowkey);
    
    public <T> EventContext<T> post(Object data, Enum<? extends EventConfig> event);
}
