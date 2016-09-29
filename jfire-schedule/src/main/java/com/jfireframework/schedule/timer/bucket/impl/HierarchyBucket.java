package com.jfireframework.schedule.timer.bucket.impl;

import com.jfireframework.schedule.handler.ExpireHandler;
import com.jfireframework.schedule.timer.Timer;
import com.jfireframework.schedule.trigger.Trigger;

public class HierarchyBucket extends AbstractBucket
{
    
    public HierarchyBucket(ExpireHandler expireHandler, Timer timer, long tickDuration_mills)
    {
        super(expireHandler, timer, tickDuration_mills);
    }
    
    @Override
    public void expire()
    {
        Node now = takeHead();
        while (now != null)
        {
            Trigger trigger = now.trigger;
            if (trigger.isCanceled())
            {
                continue;
            }
            long left = trigger.deadline() - System.currentTimeMillis();
            if (left < tickDuration_mills)
            {
                expireHandler.expire(trigger);
                trigger.calNext();
            }
            timer.add(trigger);
            now = now.next;
        }
    }
    
}
