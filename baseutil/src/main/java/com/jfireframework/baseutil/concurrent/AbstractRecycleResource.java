package com.jfireframework.baseutil.concurrent;

import java.util.Queue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractRecycleResource implements RecycleResource
{
    protected static Queue<RecycleResource> queue         = new LinkedTransferQueue<RecycleResource>();
    private AtomicInteger                   resourceCount = new AtomicInteger(0);
                                                          
    @Override
    public void acquire()
    {
        resourceCount.getAndIncrement();
    }
    
    @Override
    public void release()
    {
        int result = resourceCount.incrementAndGet();
        if (result == 0)
        {
            clear();
            queue.offer(this);
        }
        else if (result < 0)
        {
            throw new RuntimeException("资源数量已经减少到0，不可以继续在减少");
        }
    }
    
    protected abstract void clear();
}
