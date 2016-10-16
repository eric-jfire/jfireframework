package com.jfireframework.eventbus.eventcontext.impl;

import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.eventcontext.RowEventContext;
import com.jfireframework.eventbus.executor.EventHandlerExecutor;
import com.jfireframework.eventbus.handler.EventHandler;

public class RowEventContextImpl<T extends Enum<? extends EventConfig>> extends NormalEventContext<T> implements RowEventContext<T>
{
    private final Object rowkey;
    
    public RowEventContextImpl(Object eventData, T event, EventHandler<T, ?>[] combination, EventHandlerExecutor executor, EventBus eventBus, Object rowkey)
    {
        super(eventData, event, combination, executor, eventBus);
        this.rowkey = rowkey;
        if (rowkey == null)
        {
            throw new IllegalArgumentException("rowkey不能为null");
        }
    }
    
    @Override
    public Object rowkey()
    {
        return rowkey;
    }
    
}
