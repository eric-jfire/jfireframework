package com.jfireframework.context.test.function.event;

import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.event.ParallelLevel;

public enum SmsEvent implements EventConfig<SmsEvent>
{
    // 欠费
    Arrearage,
    // 停机
    halt;
    
    @Override
    public ParallelLevel parallelLevel()
    {
        return ParallelLevel.PAEALLEL;
    }
    
}
