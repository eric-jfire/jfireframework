package com.jfireframework.eventbus.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.jfireframework.baseutil.order.AescComparator;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.event.ApplicationEvent;
import com.jfireframework.eventbus.event.Event;

public abstract class AbstractEventHandlerContext<T> implements EventHandlerContext<T>
{
    protected final Enum<? extends Event<T>> event;
    protected List<EventHandler<T>>          list       = new ArrayList<EventHandler<T>>();
    protected EventHandler<T>[]              handlers;
    protected static final AescComparator    COMPARATOR = new AescComparator();
    
    public AbstractEventHandlerContext(Enum<? extends Event<T>> event)
    {
        this.event = event;
    }
    
    @Override
    public Enum<? extends Event<T>> interest()
    {
        return event;
    }
    
    @Override
    public void addHandler(EventHandler<T> eventHandler)
    {
        list.add(eventHandler);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void endAdd()
    {
        handlers = list.toArray(new EventHandler[list.size()]);
        Arrays.sort(handlers, COMPARATOR);
    }
    
    @Override
    public void handle(ApplicationEvent applicationEvent, EventBus eventBus)
    {
        try
        {
            _handler(applicationEvent, eventBus);
        }
        catch (Exception e)
        {
        }
        finally
        {
            applicationEvent.signal();
        }
    }
    
    protected abstract void _handler(ApplicationEvent applicationEvent, EventBus eventBus);
    
}
