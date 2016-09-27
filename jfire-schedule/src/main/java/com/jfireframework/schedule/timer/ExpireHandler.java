package com.jfireframework.schedule.timer;

import com.jfireframework.schedule.trigger.Trigger;

public interface ExpireHandler
{
    public void expire(Trigger trigger);
}
