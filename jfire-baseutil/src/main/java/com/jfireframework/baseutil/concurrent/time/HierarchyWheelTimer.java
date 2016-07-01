package com.jfireframework.baseutil.concurrent.time;

import java.util.concurrent.TimeUnit;
import com.jfireframework.baseutil.exception.UnSupportException;

public class HierarchyWheelTimer extends AbstractTimer
{
    private final TimeoutBucket[][] buckets;
    private final int               hierarchy;
    private final int[]             masks;
    private final long[]            tickNows;
    private final long[]            durations;
    private final long[]            capacity;
    
    public HierarchyWheelTimer(int[] hierarchies, long tickDuration, TimeUnit unit, TimeoutHandler handler)
    {
        super(tickDuration, unit, handler);
        hierarchy = hierarchies.length;
        durations = new long[hierarchy];
        capacity = new long[hierarchy];
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
        durations[0] = this.tickDuration;
        capacity[0] = durations[0] * buckets[0].length;
        for (int i = 1; i < hierarchy; i++)
        {
            durations[i] = durations[i - 1] * buckets[i - 1].length;
            capacity[i] = durations[i] * (buckets[i].length + 1);
        }
        
    }
    
    @Override
    public Timeout addTask(TimeTask task, long delay, TimeUnit unit)
    {
        start();
        Timeout timeout = new DefaultTimeout(this, task, unit.toNanos(delay) + currentTime());
        if (unit.toNanos(delay) > capacity[hierarchy - 1])
        {
            throw new UnSupportException("超时范围超出了timer的范围");
        }
        timeouts.add(timeout);
        return timeout;
    }
    
    @Override
    public void stop()
    {
        stop = true;
    }
    
    @Override
    public void run()
    {
        while (stop == false)
        {
            waitToNextTick(tickNows[0]);
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
                long left = timeout.deadline() - currentTime();
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
