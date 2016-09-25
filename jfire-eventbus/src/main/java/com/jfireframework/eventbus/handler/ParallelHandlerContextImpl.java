package com.jfireframework.eventbus.handler;

import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.event.Event;
import com.jfireframework.eventbus.eventcontext.EventContext;

public class ParallelHandlerContextImpl<T> extends AbstractEventHandlerContext<T>
{
    
    public ParallelHandlerContextImpl(Enum<? extends Event<T>> event)
    {
        super(event);
    }
    
    @Override
    public void handle(EventContext eventContext, EventBus eventBus)
    {
        try
        {
            for (EventHandler<T> each : handlers)
            {
                each.handle(eventContext, eventBus);
            }
        }
        catch (Throwable e)
        {
            eventContext.setThrowable(e);
        }
        finally
        {
            eventContext.signal();
        }
    }
    
}
