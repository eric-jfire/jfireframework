package com.jfireframework.eventbus.eventworker.impl;

import com.jfireframework.baseutil.concurrent.MPMCQueue;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.util.WorkerCount;

/**
 * io工作核心worker。在提取任务的时候不会采用超时参数。因此该worker除非整体bus关闭，否则是不会关闭
 * 
 * @author 林斌
 *
 */
public class IoCoreWorker extends IoWorker
{
    public IoCoreWorker(EventBus eventBus, //
            MPMCQueue<EventContext<?>> eventQueue, //
            WorkerCount idleCount, //
            int maxWorker)
    {
        super(eventBus, eventQueue, idleCount, maxWorker);
    }
    
    @Override
    protected EventContext<?> takeEventContext()
    {
        return eventQueue.take();
    }
    
}
