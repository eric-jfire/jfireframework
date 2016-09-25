package com.jfireframework.eventbus.eventworker.impl;

import com.jfireframework.baseutil.concurrent.MPMCQueue;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.eventbus.bus.FlexibleQueueEventBus;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.eventcontext.MoreContextInfo;
import com.jfireframework.eventbus.eventworker.EventWorker;
import com.jfireframework.eventbus.handler.EventHandlerContext;
import com.jfireframework.eventbus.util.IdleCount;

public class FlexibleCoreEventWorker implements EventWorker
{
    private final FlexibleQueueEventBus   eventBus;
    private final MPMCQueue<EventContext> eventQueue;
    private volatile Thread               ownerThread;
    private final IdleCount               idleCount;
    private static final Logger           LOGGER = ConsoleLogFactory.getLogger();
    
    public FlexibleCoreEventWorker(FlexibleQueueEventBus eventBus, //
            MPMCQueue<EventContext> eventQueue, //
            IdleCount idleCount)
    {
        this.eventBus = eventBus;
        this.eventQueue = eventQueue;
        this.idleCount = idleCount;
        idleCount.add();
    }
    
    @Override
    public void run()
    {
        ownerThread = Thread.currentThread();
        while (true)
        {
            EventContext event = eventQueue.take();
            if (event == null)
            {
                LOGGER.debug("事件线程:{}退出对事件的获取", Thread.currentThread().getName());
                break;
            }
            if (idleCount.reduce() == 0)
            {
                eventBus.addEventWorker();
            }
            EventHandlerContext<?> context = ((MoreContextInfo) event).getEventHandlerContext();
            context.handle(event, eventBus);
            idleCount.add();
        }
    }
    
    @Override
    public void stop()
    {
        ownerThread.interrupt();
    }
}
