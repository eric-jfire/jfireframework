package com.jfireframework.eventbus.bus.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import com.jfireframework.baseutil.concurrent.MPMCQueue;
import com.jfireframework.eventbus.bus.ManualEventBus;
import com.jfireframework.eventbus.eventworker.EventWorker;
import com.jfireframework.eventbus.eventworker.impl.ManualEventWorker;

public class ManualEventBusImpl extends AbstractEventBus implements ManualEventBus
{
    private final ExecutorService        pool    = Executors.newCachedThreadPool(new ThreadFactory() {
                                                     private int count = 1;
                                                     
                                                     @Override
                                                     public Thread newThread(Runnable r)
                                                     {
                                                         return new Thread(r, "ManualEventBus-eventWorker-" + (count++));
                                                     }
                                                 });
    private final MPMCQueue<EventWorker> workers = new MPMCQueue<EventWorker>();
    
    @Override
    public void stop()
    {
        pool.shutdownNow();
    }
    
    @Override
    public void createResource()
    {
        EventWorker worker = new ManualEventWorker(this, eventQueue, contextMap);
        pool.submit(worker);
        workers.offer(worker);
    }
    
    @Override
    public void recycleResource()
    {
        EventWorker worker = workers.poll();
        if (worker != null)
        {
            worker.stop();
        }
    }
    
}
