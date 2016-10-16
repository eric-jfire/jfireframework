package com.jfireframework.eventbus;

import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.event.ParallelLevel;

public enum Print implements EventConfig
{
    one(ParallelLevel.PAEALLEL), single(ParallelLevel.ROWKEY_SERIAL);
    private final ParallelLevel type;
    
    private Print(ParallelLevel type)
    {
        this.type = type;
    }
    
    @Override
    public ParallelLevel parallelLevel()
    {
        return type;
    }
    
}
