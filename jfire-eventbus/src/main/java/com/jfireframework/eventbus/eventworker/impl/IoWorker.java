package com.jfireframework.eventbus.eventworker.impl;

import com.jfireframework.baseutil.concurrent.MPMCQueue;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.eventworker.EventWorker;
import com.jfireframework.eventbus.executor.EventHandlerExecutor;
import com.jfireframework.eventbus.util.WorkerCount;

public abstract class IoWorker implements EventWorker
{
    protected final EventBus                   eventBus;
    protected final MPMCQueue<EventContext<?>> eventQueue;
    protected volatile Thread                  ownerThread;
    protected final WorkerCount                idleWorkerCount;
    protected static final Logger              LOGGER = ConsoleLogFactory.getLogger();
    protected final int                        maxWorker;
    
    public IoWorker(EventBus eventBus, //
            MPMCQueue<EventContext<?>> eventQueue, //
            WorkerCount idleCount, //
            int maxWorker)
    {
        this.eventBus = eventBus;
        this.eventQueue = eventQueue;
        this.idleWorkerCount = idleCount;
        this.maxWorker = maxWorker;
    }
    
    @Override
    public void run()
    {
        ownerThread = Thread.currentThread();
        while (true)
        {
            EventContext<?> event = takeEventContext();
            if (event == null)
            {
                LOGGER.debug("事件线程:{}退出对事件的获取", ownerThread.getName());
                idleWorkerCount.decrease();
                break;
            }
            if (idleWorkerCount.reduceIdle() == 0 && idleWorkerCount.totalWorker() < maxWorker)
            {
                eventBus.addWorker();
            }
            EventHandlerExecutor executor = event.executor();
            executor.handle(event, eventBus);
            idleWorkerCount.addIdle();
        }
    }
    
    protected abstract EventContext<?> takeEventContext();
    
    @Override
    public void stop()
    {
        ownerThread.interrupt();
    }
}
