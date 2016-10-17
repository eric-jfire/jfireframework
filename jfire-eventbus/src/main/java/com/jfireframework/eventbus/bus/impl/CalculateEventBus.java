package com.jfireframework.eventbus.bus.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import com.jfireframework.baseutil.concurrent.MPMCQueue;
import com.jfireframework.eventbus.eventworker.EventWorker;
import com.jfireframework.eventbus.eventworker.impl.CalculateWorker;

public class CalculateEventBus extends AbstractEventBus
{
    private final ExecutorService        pool    = Executors.newCachedThreadPool(new ThreadFactory() {
                                                     private int count = 1;
                                                     
                                                     @Override
                                                     public Thread newThread(Runnable r)
                                                     {
                                                         return new Thread(r, "CalculateEventBus-Worker-" + (count++));
                                                     }
                                                 });
    private final MPMCQueue<EventWorker> workers = new MPMCQueue<EventWorker>();
    
    public CalculateEventBus()
    {
        this(Runtime.getRuntime().availableProcessors() * 2 + 1);
    }
    
    public CalculateEventBus(int coreThreadNum)
    {
        for (int i = 0; i < coreThreadNum; i++)
        {
            EventWorker worker = new CalculateWorker(this, eventQueue);
            pool.submit(worker);
        }
    }
    
    @Override
    public void stop()
    {
        pool.shutdownNow();
    }
    
    @Override
    public void addWorker()
    {
        EventWorker worker = new CalculateWorker(this, eventQueue);
        pool.submit(worker);
        workers.offer(worker);
    }
    
    @Override
    public void reduceWorker()
    {
        EventWorker worker = workers.poll();
        if (worker != null)
        {
            worker.stop();
        }
    }
    
}
