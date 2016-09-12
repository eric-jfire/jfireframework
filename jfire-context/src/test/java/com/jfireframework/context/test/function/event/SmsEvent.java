package com.jfireframework.context.test.function.event;

import com.jfireframework.eventbus.event.Event;
import com.jfireframework.eventbus.event.EventType;

public enum SmsEvent implements Event<SmsEvent>
{
    // 欠费
    Arrearage,
    // 停机
    halt;
    
    @Override
    public EventType type()
    {
        return EventType.PAEALLEL;
    }
    
    @Override
    public SmsEvent instance()
    {
        return this;
    }
    
}
