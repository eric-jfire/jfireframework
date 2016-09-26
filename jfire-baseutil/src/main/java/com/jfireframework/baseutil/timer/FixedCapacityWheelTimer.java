package com.jfireframework.baseutil.timer;

import java.util.concurrent.TimeUnit;

import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.baseutil.verify.Verify;

public class FixedCapacityWheelTimer extends AbstractTimer
{
    private final int             tickCount;
    private long                  tickNow = 0;
    private final TimeoutBucket[] buckets;
    private final int             mask;
    
    public FixedCapacityWheelTimer(int tickCount, long tickDuration, TimeUnit unit)
    {
        this(tickCount, tickDuration, unit, new DefaultTimeoutHandler());
    }
    
    public FixedCapacityWheelTimer(int tickCount, long tickDuration, TimeUnit unit, TimeoutHandler handler)
    {
        super(tickDuration, unit, handler);
        int tmp = 1;
        while (tmp < tickCount && tmp > 0)
        {
            tmp = tmp << 1;
        }
        Verify.True(tmp > 0, "please check the tickCount.It is too large");
        this.tickCount = tmp;
        mask = tmp - 1;
        buckets = new TimeoutBucket[this.tickCount];
        for (int i = 0; i < buckets.length; i++)
        {
            buckets[i] = new TimeoutBucket();
        }
    }
    
    @Override
    public void run()
    {
        while (stop == false)
        {
            waitToNextTick(tickNow);
            TimeoutBucket bucket = buckets[(int) (tickNow & mask)];
            bucket.expire(handler);
            while (timeouts.isEmpty() == false)
            {
                Timeout timeout = timeouts.poll();
                long left = timeout.deadline() - currentTime();
                if (left < 0)
                {
                    handler.handle(timeout);
                    continue;
                }
                long posi = left / tickDuration;
                posi = posi == 0 ? 1 : posi;
                int index = (int) ((tickNow + posi) & mask);
                buckets[index].addTimeout(timeout);
            }
            tickNow += 1;
        }
        
    }
    
    @Override
    public Timeout addTask(TimeTask task, long delay, TimeUnit unit)
    {
        start();
        Timeout timeout = new DefaultTimeout(this, task, unit.toNanos(delay) + currentTime());
        if (unit.toNanos(delay) > tickCount * tickDuration)
        {
            throw new UnSupportException("超时范围超出了timer的范围");
        }
        timeouts.add(timeout);
        return timeout;
    }
    
}
