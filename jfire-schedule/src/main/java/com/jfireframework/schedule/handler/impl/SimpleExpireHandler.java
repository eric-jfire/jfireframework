package com.jfireframework.schedule.handler.impl;

import com.jfireframework.schedule.handler.ExpireHandler;
import com.jfireframework.schedule.trigger.Trigger;

public class SimpleExpireHandler implements ExpireHandler
{
    
    @Override
    public void expire(Trigger trigger)
    {
        if (trigger.isCanceled() == false)
        {
            trigger.timetask().invoke();
        }
    }
    
}
