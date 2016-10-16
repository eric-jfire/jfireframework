package com.jfireframework.context.event.impl;

import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import com.jfireframework.context.event.EventPoster;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.bus.impl.IoEventBus;
import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.handler.EventHandler;
import com.jfireframework.eventbus.util.AtomicIntergerIdleCount;
import com.jfireframework.eventbus.util.WorkerCount;

public class EventPosterImpl implements EventPoster
{
    @Resource
    private List<EventHandler<?>> handlers           = new LinkedList<EventHandler<?>>();
    private WorkerCount             idleCount          = new AtomicIntergerIdleCount();
    private int                   coreEventThreadNum = Runtime.getRuntime().availableProcessors();
    private long                  waitTime           = 60 * 1000;;
    private EventBus              eventBus;
    
    @PostConstruct
    public void init()
    {
        eventBus = new IoEventBus(idleCount, waitTime, coreEventThreadNum);
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
    public EventContext post(Object data, Enum<? extends EventConfig<?>> event, Object rowkey)
    {
        return eventBus.post(data, event, rowkey);
    }
    
    @Override
    public EventContext post(Object data, Enum<? extends EventConfig<?>> event)
    {
        return eventBus.post(data, event);
    }
    
}
