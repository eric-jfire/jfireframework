package com.jfireframework.eventbus.bus.impl;

import java.util.IdentityHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.eventbus.bus.FlexibleQueueEventBus;
import com.jfireframework.eventbus.event.Event;
import com.jfireframework.eventbus.eventworker.EventWorker;
import com.jfireframework.eventbus.eventworker.impl.FlexibleEventWorker;
import com.jfireframework.eventbus.handler.EventHandlerContext;
import com.jfireframework.eventbus.util.IdleCount;

public class FlexibleQueueEventBusImpl extends AbstractEventBus implements FlexibleQueueEventBus
{
    private final IdleCount       idleCount;
    private final int             coreEventThreadNum;
    private final long            waitTime;
    private final ExecutorService pool   = Executors.newCachedThreadPool(new ThreadFactory() {
                                             private int count = 1;
                                             
                                             @Override
                                             public Thread newThread(Runnable r)
                                             {
                                                 return new Thread(r, "FlexibleQueueEventBus-eventWorker-" + (count++));
                                             }
                                         });
    private static final Logger   LOGGER = ConsoleLogFactory.getLogger();
    
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
        pool.shutdownNow();
    }
    
    @Override
    public void addEventThread()
    {
        EventWorker eventThread = new FlexibleEventWorker(this, eventQueue, idleCount, coreEventThreadNum, waitTime);
        pool.submit(eventThread);
        LOGGER.debug("增加新的事件线程");
    }
    
}
