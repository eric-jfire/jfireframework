package com.jfireframework.eventbus.eventcontext.impl;

import com.jfireframework.eventbus.event.Event;
import com.jfireframework.eventbus.eventcontext.RowEventContext;

public class RowEventContextImpl extends NormalEventContext implements RowEventContext
{
    private final Object rowkey;
    
    public RowEventContextImpl(Object eventData, Enum<? extends Event<?>> event, Object rowkey)
    {
        super(eventData, event);
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
