package com.jfireframework.eventbus.bus.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import com.jfireframework.eventbus.eventworker.EventWorker;
import com.jfireframework.eventbus.eventworker.impl.IoCoreWorker;
import com.jfireframework.eventbus.eventworker.impl.IoDynamicWorker;
import com.jfireframework.eventbus.util.DefaultWorkerCount;
import com.jfireframework.eventbus.util.WorkerCount;

public class IoEventBus extends AbstractEventBus
{
    private final WorkerCount     idleCount;
    private final int             maxEventWorkerNum;
    private final long            waitTime;
    private final ExecutorService pool = Executors.newCachedThreadPool(new ThreadFactory() {
                                           private int count = 1;
                                           
                                           @Override
                                           public Thread newThread(Runnable r)
                                           {
                                               return new Thread(r, "IoEventBus-Worker-" + (count++));
                                           }
                                       });
    
    public IoEventBus(int maxEventWorkerNum, long waitTime)
    {
        this(Runtime.getRuntime().availableProcessors(), maxEventWorkerNum, waitTime);
    }
    
    public IoEventBus(int coreEventThreadNum, int maxEventWorkerNum, long waitTime)
    {
        this(new DefaultWorkerCount(), waitTime, coreEventThreadNum, maxEventWorkerNum);
    }
    
    public IoEventBus(WorkerCount workerCount, long waitTime, int coreEventWorkerNum, int maxEventWorkerNum)
    {
        this.idleCount = workerCount;
        this.maxEventWorkerNum = maxEventWorkerNum;
        this.waitTime = waitTime;
        if (coreEventWorkerNum < 1)
        {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < coreEventWorkerNum; i++)
        {
            IoCoreWorker coreEventWorker = new IoCoreWorker(this, eventQueue, workerCount, maxEventWorkerNum);
            pool.submit(coreEventWorker);
            workerCount.increase();
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
        EventWorker worker = new IoDynamicWorker(this, eventQueue, idleCount, maxEventWorkerNum, waitTime);
        pool.submit(worker);
        idleCount.increase();
    }
    
    @Override
    public void reduceWorker()
    {
        throw new UnsupportedOperationException();
    }
    
}
