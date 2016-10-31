package com.jfireframework.eventbus.executor;

import java.util.concurrent.atomic.AtomicInteger;
import com.jfireframework.baseutil.concurrent.MPSCQueue;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.event.ParallelLevel;
import com.jfireframework.eventbus.eventcontext.EventContext;

public class ReadWriteExecutor implements EventHandlerExecutor
{
    
    private AtomicInteger                    readCount = new AtomicInteger(0);
    private final MPSCQueue<EventContext<?>> events    = new MPSCQueue<EventContext<?>>();
    
    @Override
    public void handle(EventContext<?> eventContext, EventBus eventBus)
    {
        Enum<? extends EventConfig> event = eventContext.getEvent();
        // 不是读取就是写入
        if (((EventConfig) event).parallelLevel() == ParallelLevel.RW_EVENT_READ)
        {
            
        }
        else
        {
            
        }
    }
    
}
