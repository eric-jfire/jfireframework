package com.jfireframework.context.event.impl;

import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import com.jfireframework.context.event.EventPoster;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.event.ApplicationEvent;
import com.jfireframework.eventbus.event.Event;
import com.jfireframework.eventbus.handler.EventHandler;

public class EventPosterImpl implements EventPoster
{
    @Resource
    private List<EventHandler<?>> handlers = new LinkedList<EventHandler<?>>();
    private EventBus              eventBus;
    private int                   capacity;
    
    @PostConstruct
    public void init()
    {
        if (capacity <= 0)
        {
            throw new IllegalArgumentException();
        }
        eventBus = new EventBus(capacity);
        for (EventHandler<?> each : handlers)
        {
            eventBus.addHandler(each);
        }
        eventBus.start();
    }
    
    @Override
    public ApplicationEvent post(ApplicationEvent event)
    {
        return eventBus.post(event);
    }
    
    @Override
    public ApplicationEvent post(Object data, Enum<? extends Event<?>> event)
    {
        return eventBus.post(data, event);
    }
    
}
