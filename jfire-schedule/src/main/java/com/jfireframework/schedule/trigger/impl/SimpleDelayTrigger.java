package com.jfireframework.schedule.trigger.impl;

import java.util.concurrent.TimeUnit;
import com.jfireframework.schedule.task.Timetask;

public class SimpleDelayTrigger extends BaseTrigger
{
    
    public SimpleDelayTrigger(Timetask timetask, long delay, TimeUnit unit)
    {
        super(timetask);
        deadline = System.currentTimeMillis() + unit.toMillis(delay);
    }
    
    @Override
    public long deadline()
    {
        return deadline;
    }
    
    @Override
    public void calNext()
    {
        cancel();
    }
    
}
