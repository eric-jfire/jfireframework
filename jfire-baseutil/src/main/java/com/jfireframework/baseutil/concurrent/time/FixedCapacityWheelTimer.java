package com.jfireframework.baseutil.concurrent.time;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import com.jfireframework.baseutil.concurrent.MPSCLinkedQueue;
import com.jfireframework.baseutil.concurrent.UnsafeIntFieldUpdater;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.baseutil.verify.Verify;

public class FixedCapacityWheelTimer implements Timer
{
    private MPSCLinkedQueue<Timeout>                                    timeouts      = new MPSCLinkedQueue<Timeout>();
    private final long                                                  nanoTickDuration;
    private final long                                                  millTickDuration;
    private final int                                                   tickCount;
    private final TimeoutHandler                                        handler;
    private long                                                        tickNow       = 0;
    private final TimeoutBucket[]                                       buckets;
    private final int                                                   mask;
    private volatile boolean                                            stop          = false;
    private volatile int                                                state         = NOT_START;
    private static final int                                            NOT_START     = 0;
    private static final int                                            STARTED       = 1;
    private static final UnsafeIntFieldUpdater<FixedCapacityWheelTimer> state_updater = new UnsafeIntFieldUpdater<FixedCapacityWheelTimer>(FixedCapacityWheelTimer.class, "state");
    private long                                                        startTime;
    
    public FixedCapacityWheelTimer(int tickCount, long tickDuration)
    {
        this(tickCount, tickDuration, new DefaultTimeoutHandler());
    }
    
    public FixedCapacityWheelTimer(int tickCount, long tickDuration, TimeoutHandler handler)
    {
        this.handler = handler;
        nanoTickDuration = TimeUnit.MILLISECONDS.toNanos(tickDuration);
        millTickDuration = tickDuration;
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
    public void start()
    {
        if (state == NOT_START)
        {
            if (state_updater.compareAndSwap(this, NOT_START, STARTED))
            {
                startTime = System.nanoTime();
                new Thread(this).start();
            }
        }
    }
    
    @Override
    public void stop()
    {
        stop = true;
    }
    
    private void waitToNextTick()
    {
        long deadline = (tickNow + 1) * nanoTickDuration;
        for (;;)
        {
            final long currentTime = System.nanoTime() - startTime;
            // long sleepTimeMs = (deadline - currentTime + 999999) / 1000000;
            //
            // if (sleepTimeMs <= 0)
            // {
            // return;
            // }
            //
            // try
            // {
            // Thread.sleep(sleepTimeMs);
            // }
            // catch (InterruptedException ignored)
            // {
            // ;
            // }
            long sleedTimeNano = deadline - currentTime;
            if (sleedTimeNano < 0)
            {
                return;
            }
            LockSupport.parkNanos(sleedTimeNano);
        }
    }
    
    @Override
    public void run()
    {
        while (stop == false)
        {
            waitToNextTick();
            TimeoutBucket bucket = buckets[(int) (tickNow & mask)];
            bucket.expire(handler);
            while (timeouts.isEmpty() == false)
            {
                Timeout timeout = timeouts.poll();
                long left = timeout.deadline() - System.currentTimeMillis();
                if (left < 0)
                {
                    handler.handle(timeout);
                    continue;
                }
                long posi = left / millTickDuration;
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
        Timeout timeout = new DefaultTimeout(this, task, unit.toMillis(delay) + System.currentTimeMillis());
        if (unit.toMillis(delay) / millTickDuration > tickCount)
        {
            throw new UnSupportException("超时范围超出了timer的范围");
        }
        timeouts.add(timeout);
        return timeout;
    }
    
}
