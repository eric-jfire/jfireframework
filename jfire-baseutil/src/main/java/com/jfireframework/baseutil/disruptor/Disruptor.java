package com.jfireframework.baseutil.disruptor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.jfireframework.baseutil.disruptor.ringarray.ComplexMultRingArray;
import com.jfireframework.baseutil.disruptor.ringarray.RingArray;
import com.jfireframework.baseutil.disruptor.ringarray.SimpleMultRingArray;
import com.jfireframework.baseutil.disruptor.waitstrategy.WaitStrategy;

public class Disruptor
{
    public static final int       SimpleMult  = 1;
    public static final int       ComplexMult = 2;
    private final RingArray       ringArray;
    private final ExecutorService pool;
    
    public Disruptor(RingArray ringArray, ExecutorService pool)
    {
        this.pool = pool;
        this.ringArray = ringArray;
    }
    
    public Disruptor(int ringSize, EntryAction[] actions, Thread[] threads, WaitStrategy waitStrategy)
    {
        ringArray = new ComplexMultRingArray(ringSize, waitStrategy, actions);
        for (int i = 0; i < threads.length; i++)
        {
            threads[i].start();
        }
        pool = null;
    }
    
    public Disruptor(int ringSize, WaitStrategy waitStrategy, EntryAction[] actions, int ringType, ExecutorService pool)
    {
        if (actions[0] instanceof ExclusiveEntryAction)
        {
            for (EntryAction each : actions)
            {
                if (each instanceof ExclusiveEntryAction)
                {
                    ;
                }
                else
                {
                    throw new RuntimeException("action的类型必须都相同");
                }
            }
        }
        else
        {
            for (EntryAction each : actions)
            {
                if (each instanceof SharedEntryAction)
                {
                    ;
                }
                else
                {
                    throw new RuntimeException("action的类型必须都相同");
                }
            }
        }
        if (ringType == SimpleMult)
        {
            ringArray = new SimpleMultRingArray(ringSize, waitStrategy, actions);
        }
        else
        {
            ringArray = new ComplexMultRingArray(ringSize, waitStrategy, actions);
        }
        this.pool = pool;
        for (EntryAction each : actions)
        {
            pool.submit(each);
        }
    }
    
    public Disruptor(int ringSize, WaitStrategy waitStrategy, EntryAction[] actions, int ringType, int ioThreadSize)
    {
        this(ringSize, waitStrategy, actions, ringType, Executors.newFixedThreadPool(ioThreadSize));
    }
    
    public void publish(Object data)
    {
        ringArray.publish(data);
    }
    
    public void stop()
    {
        if (pool != null)
        {
            pool.shutdown();
        }
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
}
