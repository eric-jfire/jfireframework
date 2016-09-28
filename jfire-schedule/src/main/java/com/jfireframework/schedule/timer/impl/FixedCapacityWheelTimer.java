package com.jfireframework.schedule.timer.impl;

import java.util.concurrent.TimeUnit;
import com.jfireframework.baseutil.concurrent.MPSCQueue;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.schedule.timer.ExpireHandler;
import com.jfireframework.schedule.trigger.Trigger;

public class FixedCapacityWheelTimer extends BaseTimer
{
    private final int      tickCount;
    private long           tickNow = 0;
    private final Bucket[] buckets;
    private final int      mask;
    
    class Bucket
    {
        private final MPSCQueue<Trigger> triggers = new MPSCQueue<Trigger>();
        
        public void add(Trigger trigger)
        {
            triggers.offer(trigger);
        }
        
        public void expire()
        {
            long currentTime = System.currentTimeMillis();
            Trigger trigger;
            while ((trigger = triggers.poll()) != null)
            {
                long left = trigger.deadline() - currentTime;
                if (left < 0)
                {
                    expireHandler.expire(trigger);
                }
                else
                {
                   
                }
            }
        }
    }
    
    public FixedCapacityWheelTimer(int tickCount, ExpireHandler expireHandler, long tickDuration, TimeUnit unit)
    {
        super(tickDuration, unit, expireHandler);
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
            buckets[i] = new Bucket();
        }
    }
    
    @Override
    public void run()
    {
        while (state == STARTED)
        {
            waitToNextTick(tickNow);
            Bucket bucket = buckets[(int) (tickNow & mask)];
            bucket.expire();
            tickNow += 1;
        }
        
    }
    
    @Override
    protected void startTimerThread()
    {
        if (state_updater.compareAndSwap(this, BaseTimer.NOT_START, BaseTimer.STARTED))
        {
            new Thread(new Runnable() {
                
                @Override
                public void run()
                {
                    FixedCapacityWheelTimer.this.run();
                }
            }, "FixedCapacityWheelTimer-ticket-thread");
        }
    }
    
    @Override
    public void add(Trigger trigger)
    {
        long left = trigger.deadline() - baseTime;
        long posi = left / tickDuration_mills;
        posi = posi <= 0 ? 0 : posi;
        int index = (int) (posi & mask);
        buckets[index].add(trigger);
    }
}
