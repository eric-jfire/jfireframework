package com.jfireframework.eventbus.bus.impl;

import java.util.IdentityHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.eventbus.bus.FlexibleQueueEventBus;
import com.jfireframework.eventbus.event.Event;
import com.jfireframework.eventbus.eventworker.EventWorker;
import com.jfireframework.eventbus.eventworker.impl.FlexibleCoreEventWorker;
import com.jfireframework.eventbus.eventworker.impl.FlexibleEventWorker;
import com.jfireframework.eventbus.handler.EventHandlerContext;
import com.jfireframework.eventbus.util.IdleCount;

public class FlexibleQueueEventBusImpl extends AbstractEventBus implements FlexibleQueueEventBus
{
    private final IdleCount       idleCount;
    private final int             coreEventWorkerNum;
    private final int             maxEventWorkerNum;
    private final long            waitTime;
    private final ExecutorService pool        = Executors.newCachedThreadPool(new ThreadFactory() {
                                                  private int count = 1;
                                                  
                                                  @Override
                                                  public Thread newThread(Runnable r)
                                                  {
                                                      return new Thread(r, "FlexibleQueueEventBus-eventWorker-" + (count++));
                                                  }
                                              });
    private static final Logger   LOGGER      = ConsoleLogFactory.getLogger();
    private AtomicInteger         workerCount = new AtomicInteger();
    
    public FlexibleQueueEventBusImpl(IdleCount idleCount, long waitTime, int coreEventThreadNum)
    {
        this(idleCount, waitTime, coreEventThreadNum, Integer.MAX_VALUE);
    }
    
    public FlexibleQueueEventBusImpl(IdleCount idleCount, long waitTime, int coreEventWorkerNum, int maxEventWorkerNum)
    {
        this.idleCount = idleCount;
        this.maxEventWorkerNum = maxEventWorkerNum;
        this.waitTime = waitTime;
        if (coreEventWorkerNum < 1)
        {
            throw new IllegalArgumentException();
        }
        this.coreEventWorkerNum = coreEventWorkerNum;
        for (int i = 0; i < coreEventWorkerNum; i++)
        {
            FlexibleCoreEventWorker coreEventWorker = new FlexibleCoreEventWorker(this, eventQueue, idleCount);
            pool.submit(coreEventWorker);
        }
        workerCount.addAndGet(coreEventWorkerNum);
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
    public void addEventWorker()
    {
        if (workerCount.incrementAndGet() < maxEventWorkerNum)
        {
            EventWorker eventWorker = new FlexibleEventWorker(this, eventQueue, idleCount, coreEventWorkerNum, waitTime);
            pool.submit(eventWorker);
            LOGGER.debug("增加新的事件线程");
        }
        else
        {
            workerCount.decrementAndGet();
        }
    }
    
}
