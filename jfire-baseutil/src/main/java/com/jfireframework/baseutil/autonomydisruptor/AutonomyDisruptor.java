package com.jfireframework.baseutil.autonomydisruptor;

import com.jfireframework.baseutil.autonomydisruptor.waitstrategy.ParkWaitStrategy;
import com.jfireframework.baseutil.concurrent.CpuCachePadingInt;
import com.jfireframework.baseutil.disruptor.Entry;
import com.jfireframework.baseutil.disruptor.ringarray.RingArray;

public class AutonomyDisruptor
{
    private final AutonomyRingArray ringArray;
    
    public AutonomyDisruptor(int size, EntryActionFactory factory)
    {
        ringArray = new AutonomyRingArrayImpl(size, new ParkWaitStrategy(), factory);
        ringArray.addAction();
        ringArray.addAction();
        ringArray.addAction();
    }
    
    public AutonomyDisruptor(AutonomyRingArray ringArray)
    {
        this.ringArray = ringArray;
        int coresize = Runtime.getRuntime().availableProcessors();
        for (int i = 0; i < coresize; i++)
        {
            ringArray.addAction();
        }
    }
    
    public void publish(Object data)
    {
        ringArray.publish(data);
    }
    
    public void stop()
    {
        ringArray.stop();
    }
    
    public long next()
    {
        return ringArray.next();
    }
    
    public Entry entryAt(long cursor)
    {
        return ringArray.entryAt(cursor);
    }
    
    public void publish(long cursor)
    {
        ringArray.publish(cursor);
    }
    
    public RingArray getRingArray()
    {
        return ringArray;
    }
    
    public CpuCachePadingInt idleCount()
    {
        return ringArray.idleCount();
    }
}
