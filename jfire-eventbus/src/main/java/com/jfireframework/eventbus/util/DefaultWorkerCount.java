package com.jfireframework.eventbus.util;

import com.jfireframework.baseutil.concurrent.CpuCachePadingInt;

public class DefaultWorkerCount implements WorkerCount
{
    private CpuCachePadingInt idle  = new CpuCachePadingInt(0);
    private CpuCachePadingInt total = new CpuCachePadingInt(0);
    
    @Override
    public int addIdle()
    {
        return idle.increaseAndGet();
    }
    
    @Override
    public int reduceIdle()
    {
        return idle.decreaseAndGet();
    }
    
    @Override
    public int idleWorkers()
    {
        return idle.value();
    }
    
    @Override
    public void increase()
    {
        idle.increaseAndGet();
        total.increaseAndGet();
    }
    
    @Override
    public void decrease()
    {
        idle.decreaseAndGet();
        total.decreaseAndGet();
    }
    
    @Override
    public int totalWorker()
    {
        return total.value();
    }
    
}
