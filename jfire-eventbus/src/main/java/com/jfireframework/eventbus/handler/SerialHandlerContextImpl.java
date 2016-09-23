package com.jfireframework.eventbus.handler;

import java.util.concurrent.atomic.AtomicInteger;
import com.jfireframework.baseutil.concurrent.MPSCQueue;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.event.Event;
import com.jfireframework.eventbus.event.EventContext;

public class SerialHandlerContextImpl<T> extends AbstractEventHandlerContext<T>
{
    private static final int              idle   = 0;
    private static final int              busy   = 1;
    private AtomicInteger                 state  = new AtomicInteger(idle);
    private final MPSCQueue<EventContext> events = new MPSCQueue<EventContext>();
    
    public SerialHandlerContextImpl(Enum<? extends Event<T>> event)
    {
        super(event);
    }
    
    @Override
    public void handle(EventContext eventContext, EventBus eventBus)
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
