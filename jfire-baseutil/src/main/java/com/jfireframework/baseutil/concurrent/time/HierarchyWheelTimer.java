package com.jfireframework.baseutil.concurrent.time;

import java.util.concurrent.TimeUnit;
import com.jfireframework.baseutil.concurrent.MPSCLinkedQueue;
import com.jfireframework.baseutil.concurrent.UnsafeIntFieldUpdater;
import com.jfireframework.baseutil.exception.UnSupportException;

public class HierarchyWheelTimer implements Timer
{
    private final MPSCLinkedQueue<Timeout>                          timeouts      = new MPSCLinkedQueue<Timeout>();
    private final TimeoutBucket[][]                                 buckets;
    private final int                                               hierarchy;
    private final int[]                                             masks;
    private final long[]                                            tickNows;
    private volatile boolean                                        stop          = false;
    private final TimeoutHandler                                    handler;
    private final long                                              nanoTickDuration;
    private final long[]                                            durations;
    private final long[]                                            capacity;
    private volatile int                                            state         = NOT_START;
    private static final int                                        NOT_START     = 0;
    private static final int                                        STARTED       = 1;
    private static final UnsafeIntFieldUpdater<HierarchyWheelTimer> state_updater = new UnsafeIntFieldUpdater<HierarchyWheelTimer>(HierarchyWheelTimer.class, "state");
    private long                                                    startTime;
    
    public HierarchyWheelTimer(int[] hierarchies, long tickDuration, TimeoutHandler handler)
    {
        hierarchy = hierarchies.length;
        durations = new long[hierarchy];
        capacity = new long[hierarchy];
        durations[0] = tickDuration;
        this.handler = handler;
        nanoTickDuration = TimeUnit.MILLISECONDS.toNanos(tickDuration);
        masks = new int[hierarchy];
        tickNows = new long[hierarchy];
        buckets = new TimeoutBucket[hierarchies.length][];
        for (int i = 0; i < hierarchies.length; i++)
        {
            int tmp = 1;
            while (tmp < hierarchies[i] && tmp > 0)
            {
                tmp = tmp << 1;
            }
            if (tmp < 0)
            {
                throw new IllegalArgumentException();
            }
            masks[i] = tmp - 1;
            buckets[i] = new TimeoutBucket[tmp];
            for (int j = 0; j < buckets[i].length; j++)
            {
                buckets[i][j] = new TimeoutBucket();
            }
        }
        durations[0] = tickDuration;
        capacity[0] = durations[0] * buckets[0].length;
        for (int i = 1; i < hierarchy; i++)
        {
            durations[i] = durations[i - 1] * buckets[i - 1].length;
            capacity[i] = durations[i] * buckets[i].length;
        }
        
    }
    
    @Override
    public Timeout addTask(TimeTask task, long delay, TimeUnit unit)
    {
        Timeout timeout = new DefaultTimeout(this, task, unit.toMillis(delay) + System.currentTimeMillis());
        if (unit.toMillis(delay) > durations[hierarchy - 1])
        {
            throw new UnSupportException("超时范围超出了timer的范围");
        }
        timeouts.add(timeout);
        start();
        return timeout;
    }
    
    @Override
    public void stop()
    {
        stop = true;
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
    
    private void waitToNextTick()
    {
        long deadline = (tickNows[0] + 1) * nanoTickDuration;
        for (;;)
        {
            final long currentTime = System.nanoTime() - startTime;
            long sleepTimeMs = (deadline - currentTime + 999999) / 1000000;
            
            if (sleepTimeMs <= 0)
            {
                return;
            }
            
            try
            {
                Thread.sleep(sleepTimeMs);
            }
            catch (InterruptedException ignored)
            {
                ;
            }
        }
    }
    
    @Override
    public void run()
    {
        while (stop == false)
        {
            waitToNextTick();
            int index = (int) (tickNows[0] & masks[0]);
            TimeoutBucket bucket = buckets[0][index];
            bucket.expire(handler);
            if (index == masks[0])
            {
                int nowHier = 0;
                while (true)
                {
                    if (nowHier + 1 < hierarchy && index == masks[nowHier])
                    {
                        nowHier += 1;
                        index = (int) (tickNows[nowHier] & masks[nowHier]);
                        bucket = buckets[nowHier][index];
                        bucket.out(timeouts);
                        tickNows[nowHier] += 1;
                    }
                    else
                    {
                        break;
                    }
                }
            }
            while (timeouts.isEmpty() == false)
            {
                Timeout timeout = timeouts.poll();
                long left = timeout.deadline() - System.currentTimeMillis();
                if (left < 0)
                {
                    handler.handle(timeout);
                    continue;
                }
                int flag = 0;
                while (capacity[flag] < left)
                {
                    left -= capacity[flag];
                    flag += 1;
                }
                int posi = (int) (left / durations[flag]);
                if (posi == 0 && flag == 0)
                {
                    posi = 1;
                }
                int point = (int) ((tickNows[flag] + posi) & masks[flag]);
                buckets[flag][point].addTimeout(timeout);
            }
            tickNows[0] += 1;
        }
    }
    
}
