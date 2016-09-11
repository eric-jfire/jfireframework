package com.jfireframework.eventbus;

import com.jfireframework.eventbus.event.Event;
import com.jfireframework.eventbus.event.EventType;

public enum Print implements Event<Print>
{
    one(EventType.PAEALLEL);
    private final EventType type;
    
    private Print(EventType type)
    {
        this.type = type;
    }
    
    @Override
    public EventType type()
    {
        return type;
    }
    
    @Override
    public Print instance()
    {
        return this;
    }
}
