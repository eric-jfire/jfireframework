package com.jfireframework.eventbus.bus.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import com.jfireframework.baseutil.concurrent.MPMCQueue;
import com.jfireframework.eventbus.bus.ManualEventBus;
import com.jfireframework.eventbus.eventworker.EventWorker;
import com.jfireframework.eventbus.eventworker.impl.CoreWorker;

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
    
    public ManualEventBusImpl()
    {
        this(Runtime.getRuntime().availableProcessors());
    }
    
    public ManualEventBusImpl(int coreThreadNum)
    {
        for (int i = 0; i < coreThreadNum; i++)
        {
            EventWorker worker = new CoreWorker(this, eventQueue);
            pool.submit(worker);
        }
    }
    
    @Override
    public void stop()
    {
        pool.shutdownNow();
    }
    
    @Override
    public void createWorker()
    {
        EventWorker worker = new CoreWorker(this, eventQueue);
        pool.submit(worker);
        workers.offer(worker);
    }
    
    @Override
    public void recycleWorker()
    {
        EventWorker worker = workers.poll();
        if (worker != null)
        {
            worker.stop();
        }
    }
    
}
