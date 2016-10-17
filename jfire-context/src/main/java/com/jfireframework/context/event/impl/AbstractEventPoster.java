package com.jfireframework.context.event.impl;

import java.util.LinkedList;
import java.util.List;
import javax.annotation.Resource;
import com.jfireframework.context.event.EventPoster;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.handler.EventHandler;

public abstract class AbstractEventPoster implements EventPoster
{
    @Resource
    protected List<EventHandler<?, ?>> handlers = new LinkedList<EventHandler<?, ?>>();
    protected EventBus                 eventBus;
    
    @Override
    public <T extends Enum<? extends EventConfig>> EventContext<T> post(Object data, T event, Object rowkey)
    {
        return eventBus.post(data, event, rowkey);
    }
    
    @Override
    public <T extends Enum<? extends EventConfig>> EventContext<T> post(Object data, T event)
    {
        return eventBus.post(data, event);
    }
    
}
