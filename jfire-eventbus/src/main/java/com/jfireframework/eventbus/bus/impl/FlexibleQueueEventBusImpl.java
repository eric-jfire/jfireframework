package com.jfireframework.eventbus.bus.impl;

import java.util.IdentityHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import com.jfireframework.baseutil.concurrent.MPMCQueue;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.eventbus.bus.FlexibleQueueEventBus;
import com.jfireframework.eventbus.event.ApplicationEvent;
import com.jfireframework.eventbus.event.Event;
import com.jfireframework.eventbus.eventthread.EventThread;
import com.jfireframework.eventbus.eventthread.IdleCount;
import com.jfireframework.eventbus.eventthread.impl.FlexibleEventThreadImpl;
import com.jfireframework.eventbus.handler.EventHandler;
import com.jfireframework.eventbus.handler.EventHandlerContext;
import com.jfireframework.eventbus.handler.ParallelHandlerContextImpl;
import com.jfireframework.eventbus.handler.SerialHandlerContextImpl;

public class FlexibleQueueEventBusImpl implements FlexibleQueueEventBus
{
    private final MPMCQueue<ApplicationEvent>                       eventQueue = new MPMCQueue<ApplicationEvent>();
    private final IdentityHashMap<Event<?>, EventHandlerContext<?>> contextMap = new IdentityHashMap<Event<?>, EventHandlerContext<?>>();
    private final IdleCount                                         idleCount;
    private final int                                               coreEventThreadNum;
    private final long                                              waitTime;
    private final ExecutorService                                   pool       = Executors.newCachedThreadPool(
            new ThreadFactory() {
                                                                                           private int count = 1;
                                                                                           
                                                                                           @Override
                                                                                           public Thread newThread(Runnable r)
                                                                                           {
                                                                                               return new Thread(r, "FlexibleQueueEventBus-eventThread-" + (count++));
                                                                                           }
                                                                                       }
    );
    private static final Logger                                     LOGGER     = ConsoleLogFactory.getLogger();
    
    public FlexibleQueueEventBusImpl(IdleCount idleCount, long waitTime, int coreEventThreadNum)
    {
        this.idleCount = idleCount;
        this.waitTime = waitTime;
        if (coreEventThreadNum < 1)
        {
            throw new IllegalArgumentException();
        }
        this.coreEventThreadNum = coreEventThreadNum;
        for (int i = 0; i < coreEventThreadNum; i++)
        {
            addEventThread();
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> void addHandler(EventHandler<T> eventHandler)
    {
        Enum<? extends Event<T>> event = eventHandler.interest();
        EventHandlerContext<T> context = (EventHandlerContext<T>) contextMap.get(event);
        if (context == null)
        {
            switch (((Event<?>) event).type())
            {
                case PAEALLEL:
                    context = new ParallelHandlerContextImpl<T>(event);
                    break;
                case SERIAL:
                    context = new SerialHandlerContextImpl<T>(event);
                    break;
            }
            contextMap.put((Event<?>) event, context);
        }
        context.addHandler(eventHandler);
    }
    
    @Override
    public void start()
    {
        IdentityHashMap<Event<?>, EventHandlerContext<?>> copy_contextMap = new IdentityHashMap<Event<?>, EventHandlerContext<?>>(contextMap.size());
        copy_contextMap.putAll(contextMap);
        for (EventHandlerContext<?> context : copy_contextMap.values())
        {
            context.endAdd();
        }
        
    }
    
    @Override
    public void stop()
    {
        pool.shutdown();
    }
    
    @Override
    public ApplicationEvent post(ApplicationEvent event)
    {
        eventQueue.offerAndSignal(event);
        return event;
    }
    
    @Override
    public void addEventThread()
    {
        EventThread eventThread = new FlexibleEventThreadImpl(this, eventQueue, contextMap, idleCount, coreEventThreadNum, waitTime);
        pool.submit(eventThread);
        LOGGER.debug("增加新的事件线程");
    }
    
    @Override
    public ApplicationEvent post(Object data, Enum<? extends Event<?>> event)
    {
        ApplicationEvent applicationEvent = new ApplicationEvent(data, event, -1);
        post(applicationEvent);
        return applicationEvent;
    }
}
