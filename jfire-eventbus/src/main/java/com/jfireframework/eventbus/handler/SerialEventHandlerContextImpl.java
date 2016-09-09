package com.jfireframework.eventbus.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import com.jfireframework.baseutil.concurrent.MPSCLinkedQueue;
import com.jfireframework.baseutil.order.AescComparator;
import com.jfireframework.eventbus.event.ApplicationEvent;
import com.jfireframework.eventbus.event.Event;

public class SerialEventHandlerContextImpl<T> implements EventHandlerContext<T>
{
    private final Event<T>                          event;
    private List<EventHandler<T>>                   list       = new ArrayList<EventHandler<T>>();
    private EventHandler<T>[]                       handlers;
    private static final AescComparator             COMPARATOR = new AescComparator();
    private static final int                        idle       = 0;
    private static final int                        busy       = 1;
    private AtomicInteger                           state      = new AtomicInteger(idle);
    private final MPSCLinkedQueue<ApplicationEvent> taskQueue  = new MPSCLinkedQueue<ApplicationEvent>();
    
    public SerialEventHandlerContextImpl(Event<T> event)
    {
        this.event = event;
        if (event.serial() == false)
        {
            throw new NullPointerException();
        }
    }
    
    @Override
    public void addHandler(EventHandler<T> handle)
    {
        list.add(handle);
    }
    
    @Override
    public Event<T> interest()
    {
        return event;
    }
    
    @Override
    public void handle(ApplicationEvent applicationEvent)
    {
        int current = state.get();
        if (current == idle && state.compareAndSet(current, busy))
        {
            for (EventHandler<T> each : handlers)
            {
                each.handle(applicationEvent);
            }
            applicationEvent.signal();
            do
            {
                while ((applicationEvent = taskQueue.poll()) != null)
                {
                    for (EventHandler<T> each : handlers)
                    {
                        each.handle(applicationEvent);
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
                            each.handle(applicationEvent);
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
    
    @SuppressWarnings("unchecked")
    @Override
    public void endAdd()
    {
        handlers = list.toArray(new EventHandler[list.size()]);
        Arrays.sort(handlers, COMPARATOR);
    }
    
}
