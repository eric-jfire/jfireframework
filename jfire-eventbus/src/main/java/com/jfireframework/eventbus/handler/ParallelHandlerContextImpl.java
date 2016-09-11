package com.jfireframework.eventbus.handler;

import com.jfireframework.eventbus.event.ApplicationEvent;
import com.jfireframework.eventbus.event.Event;

public class ParallelHandlerContextImpl<T> extends AbstractEventHandlerContext<T>
{
    
    public ParallelHandlerContextImpl(Enum<? extends Event<T>> event)
    {
        super(event);
    }
    
    @Override
    public void handle(ApplicationEvent applicationEvent)
    {
        for (EventHandler<T> each : handlers)
        {
            each.handle(applicationEvent);
        }
        applicationEvent.signal();
    }
    
}
