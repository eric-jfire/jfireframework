package com.jfireframework.schedule.handler;

import com.jfireframework.schedule.trigger.Trigger;

public interface ExpireHandler
{
    public void expire(Trigger trigger);
}
