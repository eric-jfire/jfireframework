package com.jfireframework.baseutil.autonomydisruptor;

import java.util.concurrent.locks.LockSupport;
import com.jfireframework.baseutil.concurrent.CpuCachePadingInt;

public class TicketCount
{
    private final CpuCachePadingInt count;
    private volatile Thread         wait;
    
    public TicketCount(int total)
    {
        count = new CpuCachePadingInt(total);
    }
    
    public void countDown()
    {
        if (count.decreaseAndGet() == 0)
        {
            LockSupport.unpark(wait);
        }
    }
    
    public void await()
    {
        wait = Thread.currentThread();
        while (count.value() > 0)
        {
            LockSupport.park();
        }
    }
}
