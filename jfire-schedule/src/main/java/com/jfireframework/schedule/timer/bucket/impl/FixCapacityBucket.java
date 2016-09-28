package com.jfireframework.schedule.timer.bucket.impl;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import com.jfireframework.schedule.timer.ExpireHandler;
import com.jfireframework.schedule.timer.Timer;
import com.jfireframework.schedule.trigger.Trigger;

public class FixCapacityBucket extends AbstractBucket
{
    private final Lock lock = new ReentrantLock();
    
    public FixCapacityBucket(ExpireHandler expireHandler, Timer timer)
    {
        super(expireHandler, timer);
    }
    
    @Override
    public void expire()
    {
        lock.lock();
        try
        {
            long currentTime = System.currentTimeMillis();
            for (Node predNotTrigger = null, now = head; now != null;)
            {
                Trigger trigger = now.trigger;
                long left = trigger.deadline() - currentTime;
                if (left < 0)
                {
                    expireHandler.expire(trigger);
                    trigger.calNextline();
                    // 这一步还是比较重的。所以整体的expire应该考虑如何分化。
                    timer.add(trigger);
                    /**
                     * 只移动now，就相当于删除了这个trigger
                     */
                    now = now.next;
                }
                else
                {
                    if (predNotTrigger == null)
                    {
                        predNotTrigger = head;
                        now = predNotTrigger.next;
                    }
                    else
                    {
                        predNotTrigger.next = now;
                        predNotTrigger = now;
                        now = predNotTrigger.next;
                    }
                }
            }
        }
        finally
        {
            lock.unlock();
        }
    }
}
