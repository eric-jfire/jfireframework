package com.jfireframework.schedule.timer.bucket.impl;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import com.jfireframework.schedule.handler.ExpireHandler;
import com.jfireframework.schedule.timer.Timer;
import com.jfireframework.schedule.trigger.Trigger;

public class HierarchyBucket extends AbstractBucket
{
    
    private final Lock lock = new ReentrantLock();
    
    public HierarchyBucket(ExpireHandler expireHandler, Timer timer, long tickDuration_mills)
    {
        super(expireHandler, timer, tickDuration_mills);
    }
    
    @Override
    public void expire()
    {
        lock.lock();
        try
        {
            long currentTime = System.currentTimeMillis();
            Node start = head;
            for (Node now = start; now != null; now = now.next)
            {
                Trigger trigger = now.trigger;
                if (trigger.isCanceled())
                {
                    continue;
                }
                long left = trigger.deadline() - currentTime;
                if (left < 0)
                {
                    expireHandler.expire(trigger);
                    trigger.calNext();
                }
                timer.add(trigger);
            }
            head.next = null;
        }
        finally
        {
            lock.unlock();
        }
        
    }
    
}
