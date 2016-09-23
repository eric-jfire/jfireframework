package com.jfireframework.context.event.impl;

import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import com.jfireframework.context.event.EventPoster;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.bus.impl.FlexibleQueueEventBusImpl;
import com.jfireframework.eventbus.event.Event;
import com.jfireframework.eventbus.event.EventContext;
import com.jfireframework.eventbus.eventthread.AtomicIntergerIdleCount;
import com.jfireframework.eventbus.eventthread.IdleCount;
import com.jfireframework.eventbus.handler.EventHandler;

public class EventPosterImpl implements EventPoster
{
    @Resource
    private List<EventHandler<?>> handlers           = new LinkedList<EventHandler<?>>();
    private IdleCount             idleCount          = new AtomicIntergerIdleCount();
    private int                   coreEventThreadNum = Runtime.getRuntime().availableProcessors();
    private long                  waitTime           = 60 * 1000;;
    private EventBus              eventBus;
    
    @PostConstruct
    public void init()
    {
        eventBus = new FlexibleQueueEventBusImpl(idleCount, waitTime, coreEventThreadNum);
        for (EventHandler<?> handler : handlers)
        {
            eventBus.addHandler(handler);
        }
        eventBus.start();
    }
    
    @Override
    public EventContext post(EventContext event)
    {
        return eventBus.post(event);
    }
    
    @Override
    public EventContext post(Object data, Enum<? extends Event<?>> event, Object rowkey)
    {
        return eventBus.post(data, event, rowkey);
    }
    
    @Override
    public EventContext post(Object data, Enum<? extends Event<?>> event)
    {
        return eventBus.post(data, event);
    }
    
}
