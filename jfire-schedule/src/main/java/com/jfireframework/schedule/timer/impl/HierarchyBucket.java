package com.jfireframework.schedule.timer.impl;

import com.jfireframework.schedule.timer.ExpireHandler;
import com.jfireframework.schedule.timer.Timer;
import com.jfireframework.schedule.timer.bucket.impl.AbstractBucket;

public class HierarchyBucket extends AbstractBucket
{
    
    public HierarchyBucket(ExpireHandler expireHandler, Timer timer)
    {
        super(expireHandler, timer);
    }
    
    @Override
    public void expire()
    {
        // TODO Auto-generated method stub
        
    }
    
}
