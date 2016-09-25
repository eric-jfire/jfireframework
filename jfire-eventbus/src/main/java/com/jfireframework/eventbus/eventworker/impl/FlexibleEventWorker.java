package com.jfireframework.eventbus.eventworker.impl;

import java.util.concurrent.TimeUnit;
import com.jfireframework.baseutil.concurrent.MPMCQueue;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.eventbus.bus.FlexibleQueueEventBus;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.eventcontext.MoreContextInfo;
import com.jfireframework.eventbus.eventworker.EventWorker;
import com.jfireframework.eventbus.handler.EventHandlerContext;
import com.jfireframework.eventbus.util.IdleCount;

public class FlexibleEventWorker implements EventWorker
{
    private final FlexibleQueueEventBus   eventBus;
    private final MPMCQueue<EventContext> eventQueue;
    private volatile Thread               ownerThread;
    private final IdleCount               idleCount;
    private final int                     coreEventThreadNum;
    private final long                    waitTime;
    private static final Logger           LOGGER = ConsoleLogFactory.getLogger();
    
    public FlexibleEventWorker(FlexibleQueueEventBus eventBus, //
            MPMCQueue<EventContext> eventQueue, //
            IdleCount idleCount, //
            int coreEventThreadNum, //
            long waitTime)
    {
        this.eventBus = eventBus;
        this.waitTime = waitTime;
        this.eventQueue = eventQueue;
        this.idleCount = idleCount;
        this.coreEventThreadNum = coreEventThreadNum;
        idleCount.add();
    }
    
    @Override
    public void run()
    {
        ownerThread = Thread.currentThread();
        while (true)
        {
            EventContext event = eventQueue.take(waitTime, TimeUnit.MILLISECONDS);
            if (event == null)
            {
                if (idleCount.reduce() > coreEventThreadNum)
                {
                    LOGGER.debug("事件线程:{}退出对事件的获取", Thread.currentThread().getName());
                    break;
                }
                else
                {
                    idleCount.add();
                    continue;
                }
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
