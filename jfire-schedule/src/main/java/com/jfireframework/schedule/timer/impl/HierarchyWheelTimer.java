package com.jfireframework.schedule.timer.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import com.jfireframework.baseutil.concurrent.MPSCQueue;
import com.jfireframework.schedule.handler.ExpireHandler;
import com.jfireframework.schedule.timer.bucket.Bucket;
import com.jfireframework.schedule.timer.bucket.impl.HierarchyBucket;
import com.jfireframework.schedule.trigger.Trigger;

public class HierarchyWheelTimer extends BaseTimer
{
    private final Bucket[][]         buckets;
    private final int                level;
    private final int[]              masks;
    private final long[]             tickNows;
    /**
     * 每个层级的时间间隔
     */
    private final long[]             durations;
    private final long[]             thresholds;
    private final ExecutorService    pool;
    private final MPSCQueue<Trigger> tooBigTriggers = new MPSCQueue<Trigger>();
    
    public HierarchyWheelTimer(int[] hierarchies, ExpireHandler expireHandler, long tickDuration, TimeUnit unit)
    {
        this(hierarchies, expireHandler, Executors.newCachedThreadPool(), tickDuration, unit);
    }
    
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
            long tickDuration_mills = unit.toMillis(tickDuration);
            for (int j = 0; j < buckets[i].length; j++)
            {
                buckets[i][j] = new HierarchyBucket(expireHandler, this, tickDuration_mills);
            }
        }
        durations[0] = unit.toMillis(tickDuration);
        for (int i = 1; i < level; i++)
        {
            durations[i] = durations[i - 1] * buckets[i - 1].length;
        }
        thresholds = new long[level];
        for (int i = 0; i < level; i++)
        {
            thresholds[i] = durations[i] * buckets[i].length;
        }
        new Thread(this, "HierarchyWheelTimer").start();
    }
    
    @Override
    public void run()
    {
        while (state == termination)
        {
            waitToNextTick(tickNows[0]);
            final int index = (int) (tickNows[0] & masks[0]);
            final Bucket bucket = buckets[0][index];
            pool.execute(new Runnable() {
                @Override
                public void run()
                {
                    bucket.expire();
                }
            });
            tickNows[0] += 1;
            if (index == masks[0])
            {
                int nowLevel = 1;
                for (; nowLevel < level; nowLevel++)
                {
                    int highLevelIndex = (int) (tickNows[nowLevel] & masks[nowLevel]);
                    final Bucket highLevelBucket = buckets[nowLevel][highLevelIndex];
                    pool.execute(new Runnable() {
                        @Override
                        public void run()
                        {
                            highLevelBucket.expire();
                        }
                    });
                    tickNows[nowLevel] += 1;
                    if (highLevelIndex == masks[nowLevel])
                    {
                        ;
                    }
                    else
                    {
                        break;
                    }
                }
                if (nowLevel == level)
                {
                    pool.execute(new Runnable() {
                        @Override
                        public void run()
                        {
                            List<Trigger> tmp = new LinkedList<Trigger>();
                            long threshold = thresholds[level - 1];
                            for (Trigger trigger = tooBigTriggers.poll(); trigger != null; trigger = tooBigTriggers.poll())
                            {
                                if (trigger.deadline() - System.currentTimeMillis() < threshold)
                                {
                                    add(trigger);
                                }
                                else
                                {
                                    tmp.add(trigger);
                                }
                            }
                            for (Trigger each : tmp)
                            {
                                tooBigTriggers.offer(each);
                            }
                        }
                        
                    });
                }
            }
        }
    }
    
    @Override
    public void add(Trigger trigger)
    {
        if (trigger.isCanceled())
        {
            return;
        }
        long left = trigger.deadline() - System.currentTimeMillis();
        if (left > thresholds[level - 1])
        {
            tooBigTriggers.offer(trigger);
        }
        else
        {
            for (int i = 0; i < level; i++)
            {
                if (left > thresholds[i])
                {
                    continue;
                }
                else
                {
                    int posi = (int) (left / durations[i]);
                    buckets[i][posi].add(trigger);
                    break;
                }
            }
        }
    }
    
}
