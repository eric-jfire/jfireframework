package com.jfireframework.eventbus.bus;

import java.util.IdentityHashMap;
import com.jfireframework.baseutil.disruptor.AbstractExclusiveEntryAction;
import com.jfireframework.eventbus.event.ApplicationEvent;
import com.jfireframework.eventbus.event.Event;
import com.jfireframework.eventbus.handler.EventHandlerContext;

public class EventAction extends AbstractExclusiveEntryAction
{
    private final IdentityHashMap<Event<?>, EventHandlerContext<?>> contextMap;
    
    public EventAction(IdentityHashMap<Event<?>, EventHandlerContext<?>> contextMap)
    {
        this.contextMap = contextMap;
    }
    
    @Override
    public <T> void doJob(T data)
    {
        ApplicationEvent event = (ApplicationEvent) data;
        EventHandlerContext<?> context = contextMap.get(event.getEvent());
        if (context != null)
        {
            context.handle(event);
        }
    }
    
}
