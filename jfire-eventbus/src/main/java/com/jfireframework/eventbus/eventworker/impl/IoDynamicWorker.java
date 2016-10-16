package com.jfireframework.eventbus.eventworker.impl;

import java.util.concurrent.TimeUnit;
import com.jfireframework.baseutil.concurrent.MPMCQueue;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.util.WorkerCount;

/**
 * 处理io类型的worker。特点是在提取任务的时候采用了超时参数。也就是说一段时间无法提取到任务则自动关闭该worker
 * 
 * @author 林斌
 *
 */
public class IoDynamicWorker extends IoWorker
{
    private final long waitTime;
    
    public IoDynamicWorker(EventBus eventBus, //
            MPMCQueue<EventContext<?>> eventQueue, //
            WorkerCount idleCount, //
            int maxWorker, //
            long waitTime)
    {
        super(eventBus, eventQueue, idleCount, maxWorker);
        this.waitTime = waitTime;
    }
    
    @Override
    protected EventContext<?> takeEventContext()
    {
        return eventQueue.take(waitTime, TimeUnit.MILLISECONDS);
    }
    
}
