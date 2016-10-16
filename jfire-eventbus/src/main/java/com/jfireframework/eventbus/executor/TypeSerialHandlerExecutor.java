package com.jfireframework.eventbus.executor;

import java.util.concurrent.atomic.AtomicInteger;
import com.jfireframework.baseutil.concurrent.MPSCQueue;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.handler.EventHandler;

public class TypeSerialHandlerExecutor implements EventHandlerExecutor
{
    
    private static final int                 idle   = 0;
    private static final int                 busy   = 1;
    private AtomicInteger                    state  = new AtomicInteger(idle);
    private final MPSCQueue<EventContext<?>> events = new MPSCQueue<EventContext<?>>();
    
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void handle(EventContext<?> eventContext, EventBus eventBus)
    {
        events.offer(eventContext);
        do
        {
            int current = state.get();
            if (current == idle && state.compareAndSet(current, busy))
            {
                while ((eventContext = events.poll()) != null)
                {
                    try
                    {
                        Object trans = eventContext.getEventData();
                        for (EventHandler each : eventContext.combinationHandlers())
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
                state.set(idle);
                if (events.isEmpty())
                {
                    break;
                }
                else
                {
                    continue;
                }
            }
        } while (true);
        
    }
    
}
