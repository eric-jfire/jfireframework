package com.jfireframework.schedule.timer.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.schedule.handler.ExpireHandler;
import com.jfireframework.schedule.timer.bucket.Bucket;
import com.jfireframework.schedule.timer.bucket.impl.FixCapacityBucket;
import com.jfireframework.schedule.trigger.Trigger;

public class FixedCapacityWheelTimer extends BaseTimer
{
    private final int             tickCount;
    private long                  tickNow = 0;
    private final Bucket[]        buckets;
    private final int             mask;
    private final ExecutorService pool;
    protected final long          tickDuration_mills;
    
    public FixedCapacityWheelTimer(int tickCount, ExpireHandler expireHandler, ExecutorService pool, long tickDuration, TimeUnit unit)
    {
        super(expireHandler, tickDuration, unit);
        tickDuration_mills = unit.toMillis(tickDuration);
        this.pool = pool;
        int tmp = 1;
        while (tmp < tickCount && tmp > 0)
        {
            tmp = tmp << 1;
        }
        Verify.True(tmp > 0, "please check the tickCount. It is too large");
        this.tickCount = tmp;
        mask = tmp - 1;
        buckets = new Bucket[this.tickCount];
        for (int i = 0; i < buckets.length; i++)
        {
            buckets[i] = new FixCapacityBucket(expireHandler, this, tickDuration_mills);
        }
        new Thread(this, "FixedCapacityWheelTimer").start();
    }
    
    public FixedCapacityWheelTimer(int tickCount, ExpireHandler expireHandler, long tickDuration, TimeUnit unit)
    {
        this(tickCount, expireHandler, Executors.newCachedThreadPool(), tickDuration, unit);
    }
    
    @Override
    public void run()
    {
        while (state == STARTED)
        {
            final Bucket bucket = buckets[(int) (tickNow & mask)];
            pool.execute(
                    new Runnable() {
                        @Override
                        public void run()
                        {
                            bucket.expire();
                        }
                    }
            );
            waitToNextTick(tickNow);
            tickNow += 1;
        }
        
    }
    
    @Override
    public void add(Trigger trigger)
    {
        if (trigger.isCanceled())
        {
            return;
        }
        long left = trigger.deadline() - baseTime;
        long posi = left / tickDuration_mills;
        posi = posi <= 0 ? 0 : posi;
        int index = (int) (posi & mask);
        buckets[index].add(trigger);
    }
    
}
