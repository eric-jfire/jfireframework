package com.jfireframework.schedule.timer.bucket.impl;

import com.jfireframework.schedule.handler.ExpireHandler;
import com.jfireframework.schedule.timer.Timer;
import com.jfireframework.schedule.trigger.Trigger;

public class FixCapacityBucket extends AbstractBucket
{
    public FixCapacityBucket(ExpireHandler expireHandler, Timer timer, long tickDuration_mills)
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
                ;
            }
            else
            {
                long left = trigger.deadline() - System.currentTimeMillis();
                if (left < tickDuration_mills)
                {
                    expireHandler.expire(trigger);
                    trigger.calNext();
                    // 这一步还是比较重的。所以整体的expire应该考虑如何分化。
                    timer.add(trigger);
                }
                else
                {
                    this.add(trigger);
                }
            }
            now = now.next;
        }
    }
}
