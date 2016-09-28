package com.jfireframework.schedule.timer.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import com.jfireframework.schedule.timer.ExpireHandler;
import com.jfireframework.schedule.timer.bucket.Bucket;

public class HierarchyWheelTimer extends BaseTimer
{
    private final Bucket[][]      buckets;
    private final int             level;
    private final int[]           masks;
    private final long[]          tickNows;
    private final long[]          durations;
    private final ExecutorService pool;
    
    public HierarchyWheelTimer(int[] hierarchies, ExpireHandler expireHandler, ExecutorService pool, long tickDuration, TimeUnit unit)
    {
        super(expireHandler, tickDuration, unit);
        this.pool = pool;
        level = hierarchies.length;
        durations = new long[level];
        masks = new int[level];
        tickNows = new long[level];
        buckets = new Bucket[level][];
        for (int i = 0; i < level; i++)
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
            buckets[i] = new Bucket[tmp];
            for (int j = 0; j < buckets[i].length; j++)
            {
                buckets[i][j] = new HierarchyBucket(expireHandler, this);
            }
        }
        durations[0] = unit.toMillis(tickDuration);
        for (int i = 1; i < level; i++)
        {
            durations[i] = durations[i - 1] * buckets[i - 1].length;
        }
        
    }
    
    @Override
    public void run()
    {
        while (state == termination)
        {
            waitToNextTick(tickNows[0]);
            final int index = (int) (tickNows[0] & masks[0]);
            final Bucket bucket = buckets[0][index];
            pool.execute(
                    new Runnable() {
                        @Override
                        public void run()
                        {
                            bucket.expire();
                        }
                    }
            );
            tickNows[0] = index + 1;
            if (index == 0 && tickNows[0] != 0)
            {
                for (int i = 1; i < level; i++)
                {
                    final long highLevelIndex = tickNows[i];
                    final Bucket highLevelBucket = buckets[i][(int) (highLevelIndex & masks[i])];
                    pool.execute(
                            new Runnable() {
                                @Override
                                public void run()
                                {
                                    highLevelBucket.expire();
                                }
                            }
                    );
                    tickNows[i] = highLevelIndex + 1;
                    if (highLevelIndex == 0 && tickNows[i] != 0)
                    {
                        ;
                    }
                    else
                    {
                        break;
                    }
                }
            }
        }
    }
    
}
