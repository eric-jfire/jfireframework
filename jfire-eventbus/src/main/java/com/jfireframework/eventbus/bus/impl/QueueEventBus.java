package com.jfireframework.eventbus.bus.impl;

import com.jfireframework.baseutil.concurrent.MPMCQueue;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.event.ApplicationEvent;
import com.jfireframework.eventbus.event.Event;
import com.jfireframework.eventbus.event.EventType;
import com.jfireframework.eventbus.handler.*;

import java.util.IdentityHashMap;

/**
 * Created by linbin on 2016/9/19.
 */
public class QueueEventBus implements EventBus
{
    private final MPMCQueue<ApplicationEvent>                       eventQueue = new MPMCQueue<ApplicationEvent>();
    
    private final IdentityHashMap<Event<?>, EventHandlerContext<?>> contextMap = new IdentityHashMap<Event<?>, EventHandlerContext<?>>();
    
    @Override
    public <T> void addHandler(EventHandler<T> eventHandler)
    {
        Enum<? extends Event<T>> event = eventHandler.interest();
        EventHandlerContext<T> context = (EventHandlerContext<T>) contextMap.get(event);
        if (context == null)
        {
            switch (((Event<?>) event).type())
            {
                case PAEALLEL:
                    context = new ParallelHandlerContextImpl<T>(event);
                    break;
                case SERIAL:
                    context = new SerialHandlerContextImpl<T>(event);
                    break;
                case ROWID_SERIAL:
                    context = new RowidSerialHandlerContextImpl<T>(event);
                    break;
            }
            contextMap.put((Event<?>) event, context);
        }
        context.addHandler(eventHandler);
    }
    
    @Override
    public void start()
    {
        IdentityHashMap<Event<?>, EventHandlerContext<?>> copy_contextMap = new IdentityHashMap<Event<?>, EventHandlerContext<?>>(contextMap.size());
        copy_contextMap.putAll(contextMap);
        for (EventHandlerContext<?> context : copy_contextMap.values())
        {
            context.endAdd();
        }

    }
    
    @Override
    public void stop()
    {
        
    }
    
    @Override
    public ApplicationEvent post(ApplicationEvent event)
    {
        return null;
    }
    
    @Override
    public ApplicationEvent post(Object data, Enum<? extends Event<?>> event)
    {
        if (((Event<?>) event).type() == EventType.ROWID_SERIAL)
        {
            throw new IllegalArgumentException();
        }
        ApplicationEvent applicationEvent = new ApplicationEvent(data, event, -1);
        post(applicationEvent);
        return applicationEvent;
    }
}
