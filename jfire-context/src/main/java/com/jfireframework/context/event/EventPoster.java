package com.jfireframework.context.event;

import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.eventcontext.EventContext;

public interface EventPoster
{
    public <T extends Enum<? extends EventConfig>> EventContext<T> post(Object data, T event, Object rowkey);
    
    public <T extends Enum<? extends EventConfig>> EventContext<T> post(Object data, T event);
}
