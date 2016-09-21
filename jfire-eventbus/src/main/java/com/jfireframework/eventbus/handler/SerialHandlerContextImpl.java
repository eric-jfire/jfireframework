package com.jfireframework.eventbus.handler;

import java.util.concurrent.atomic.AtomicInteger;
import com.jfireframework.baseutil.concurrent.MPSCLinkedQueue;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.event.ApplicationEvent;
import com.jfireframework.eventbus.event.Event;

public class SerialHandlerContextImpl<T> extends AbstractEventHandlerContext<T>
{
    private static final int                        idle      = 0;
    private static final int                        busy      = 1;
    private AtomicInteger                           state     = new AtomicInteger(idle);
    private final MPSCLinkedQueue<ApplicationEvent> taskQueue = new MPSCLinkedQueue<ApplicationEvent>();
    
    public SerialHandlerContextImpl(Enum<? extends Event<T>> event)
    {
        super(event);
    }
    
    @Override
    public void handle(ApplicationEvent applicationEvent, EventBus eventBus)
    {
        int current = state.get();
        if (current == idle && state.compareAndSet(current, busy))
        {
            for (EventHandler<T> each : handlers)
            {
                each.handle(applicationEvent, eventBus);
            }
            applicationEvent.signal();
            do
            {
                while ((applicationEvent = taskQueue.poll()) != null)
                {
                    for (EventHandler<T> each : handlers)
                    {
                        each.handle(applicationEvent, eventBus);
                    }
                    applicationEvent.signal();
                }
                state.set(idle);
                if (taskQueue.isEmpty())
                {
                    break;
                }
                else
                {
                    current = state.get();
                    if (current == idle && state.compareAndSet(current, busy))
                    {
                        continue;
                    }
                    else
                    {
                        break;
                    }
                }
            } while (true);
        }
        else
        {
            taskQueue.add(applicationEvent);
            do
            {
                current = state.get();
                if (current == idle && state.compareAndSet(current, busy))
                {
                    while ((applicationEvent = taskQueue.poll()) != null)
                    {
                        for (EventHandler<T> each : handlers)
                        {
                            each.handle(applicationEvent, eventBus);
                        }
                        applicationEvent.signal();
                    }
                    state.set(idle);
                    if (taskQueue.isEmpty())
                    {
                        break;
                    }
                    else
                    {
                        continue;
                    }
                }
                else
                {
                    break;
                }
            } while (true);
        }
    }
    
}
