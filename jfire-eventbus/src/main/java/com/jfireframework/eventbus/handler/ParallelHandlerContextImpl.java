package com.jfireframework.eventbus.handler;

import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.event.ApplicationEvent;
import com.jfireframework.eventbus.event.Event;

public class ParallelHandlerContextImpl<T> extends AbstractEventHandlerContext<T>
{
    
    public ParallelHandlerContextImpl(Enum<? extends Event<T>> event)
    {
        super(event);
    }
    
    @Override
    protected void _handler(ApplicationEvent applicationEvent, EventBus eventBus)
    {
        for (EventHandler<T> each : handlers)
        {
            each.handle(applicationEvent, eventBus);
        }
        applicationEvent.signal();
    }
    
}
