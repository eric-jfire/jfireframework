package com.jfireframework.eventbus;

import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.event.ParallelLevel;

public enum Speed implements EventConfig
{
    speed;
    
    @Override
    public ParallelLevel parallelLevel()
    {
        return ParallelLevel.ROWKEY_SERIAL;
    }
}
