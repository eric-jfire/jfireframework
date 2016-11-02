package com.jfireframework.eventbus.readwrite;

import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.event.ParallelLevel;

public enum ReadWriteEvent implements EventConfig
{
    read(ParallelLevel.RW_EVENT_READ), write(ParallelLevel.RW_EVENT_WRITE);
    private final ParallelLevel level;
    
    private ReadWriteEvent(ParallelLevel level)
    {
        this.level = level;
    }
    
    @Override
    public ParallelLevel parallelLevel()
    {
        return level;
    }
    
}
