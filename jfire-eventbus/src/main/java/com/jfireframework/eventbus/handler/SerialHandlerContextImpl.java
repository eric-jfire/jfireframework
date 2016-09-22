package com.jfireframework.eventbus.handler;

import java.util.concurrent.atomic.AtomicInteger;
import com.jfireframework.baseutil.concurrent.MPSCQueue;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.event.ApplicationEvent;
import com.jfireframework.eventbus.event.Event;

public class SerialHandlerContextImpl<T> extends AbstractEventHandlerContext<T>
{
    private static final int                  idle   = 0;
    private static final int                  busy   = 1;
    private AtomicInteger                     state  = new AtomicInteger(idle);
    private final MPSCQueue<ApplicationEvent> events = new MPSCQueue<ApplicationEvent>();
    
    public SerialHandlerContextImpl(Enum<? extends Event<T>> event)
    {
        super(event);
    }
    
    @Override
    public void handle(ApplicationEvent applicationEvent, EventBus eventBus)
    {
        events.offer(applicationEvent);
        do
        {
            int current = state.get();
            if (current == idle && state.compareAndSet(current, busy))
            {
                while ((applicationEvent = events.poll()) != null)
                {
                    try
                    {
                        for (EventHandler<T> each : handlers)
                        {
                            each.handle(applicationEvent, eventBus);
                        }
                    }
                    catch (Throwable e)
                    {
                        applicationEvent.setThrowable(e);
                    }
                    finally
                    {
                        applicationEvent.signal();
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
