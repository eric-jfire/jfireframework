package com.jfireframework.eventbus.eventworker.impl;

import com.jfireframework.baseutil.concurrent.MPMCQueue;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.eventcontext.MoreContextInfo;
import com.jfireframework.eventbus.eventworker.EventWorker;
import com.jfireframework.eventbus.handler.EventHandlerContext;

public class ManualEventWorker implements EventWorker
{
    
    private final EventBus                eventBus;
    private final MPMCQueue<EventContext> eventQueue;
    private volatile Thread               ownerThread;
    private static final Logger           LOGGER = ConsoleLogFactory.getLogger();
    
    public ManualEventWorker(EventBus eventBus, //
            MPMCQueue<EventContext> eventQueue)
    {
        this.eventBus = eventBus;
        this.eventQueue = eventQueue;
    }
    
    @Override
    public void run()
    {
        ownerThread = Thread.currentThread();
        while (true)
        {
            EventContext eventContext = eventQueue.take();
            if (eventContext == null)
            {
                LOGGER.debug("事件线程:{}退出对事件的获取", Thread.currentThread().getName());
                break;
            }
            EventHandlerContext<?> context = ((MoreContextInfo) eventContext).getEventHandlerContext();
            context.handle(eventContext, eventBus);
        }
    }
    
    @Override
    public void stop()
    {
        ownerThread.interrupt();
    }
    
}
