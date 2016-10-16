package com.jfireframework.eventbus.executor;

import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.handler.EventHandler;

public class ParallelHandlerExecutor implements EventHandlerExecutor
{
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void handle(EventContext<?> eventContext, EventBus eventBus)
    {
        try
        {
            EventHandler<?, ?>[] handlers = eventContext.combinationHandlers();
            Object trans = eventContext.getEventData();
            for (EventHandler each : handlers)
            {
                trans = each.handle(trans, eventBus);
            }
            eventContext.setResult(trans);
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
