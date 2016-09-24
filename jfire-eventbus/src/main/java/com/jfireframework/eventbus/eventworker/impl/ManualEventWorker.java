package com.jfireframework.eventbus.eventworker.impl;

import java.util.IdentityHashMap;
import com.jfireframework.baseutil.concurrent.MPMCQueue;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.event.Event;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.eventworker.EventWorker;
import com.jfireframework.eventbus.handler.EventHandlerContext;

public class ManualEventWorker implements EventWorker
{
    
    private final EventBus                                          eventBus;
    private final MPMCQueue<EventContext>                           eventQueue;
    private volatile Thread                                         ownerThread;
    private final IdentityHashMap<Event<?>, EventHandlerContext<?>> contextMap;
    private static final Logger                                     LOGGER = ConsoleLogFactory.getLogger();
    
    public ManualEventWorker(EventBus eventBus, //
            MPMCQueue<EventContext> eventQueue, //
            IdentityHashMap<Event<?>, EventHandlerContext<?>> contextMap)
    {
        this.eventBus = eventBus;
        this.eventQueue = eventQueue;
        this.contextMap = contextMap;
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
            EventHandlerContext<?> context = contextMap.get(event.getEvent());
            if (context != null)
            {
                context.handle(event, eventBus);
            }
        }
    }
    
    @Override
    public void stop()
    {
        ownerThread.interrupt();
    }
    
}
