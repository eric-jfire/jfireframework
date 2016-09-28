package com.jfireframework.schedule.timer.bucket;

import com.jfireframework.schedule.trigger.Trigger;

public interface Bucket
{
    void add(Trigger trigger);
    
    void expire();
}
