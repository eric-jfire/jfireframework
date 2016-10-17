package com.jfireframework.eventbus.eventworker.impl;

import com.jfireframework.baseutil.concurrent.MPMCQueue;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.eventworker.EventWorker;
import com.jfireframework.eventbus.executor.EventHandlerExecutor;

/**
 * 主要用于计算任务的worker。该worker不关心当前的worker数量
 * 
 * @author 林斌
 *
 */
public class CalculateWorker implements EventWorker
{
    private final EventBus                   eventBus;
    private final MPMCQueue<EventContext<?>> eventQueue;
    private volatile Thread                  ownerThread;
    private static final Logger              LOGGER = ConsoleLogFactory.getLogger();
    
    public CalculateWorker(EventBus eventBus, MPMCQueue<EventContext<?>> eventQueue)
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
            EventContext<?> eventContext = eventQueue.take();
            if (eventContext == null)
            {
                LOGGER.debug("事件线程:{}退出对事件的获取", ownerThread);
                break;
            }
            EventHandlerExecutor executor = eventContext.executor();
            executor.handle(eventContext, eventBus);
        }
    }
    
    @Override
    public void stop()
    {
        ownerThread.interrupt();
    }
    
}
