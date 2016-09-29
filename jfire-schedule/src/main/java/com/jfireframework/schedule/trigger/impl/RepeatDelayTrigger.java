package com.jfireframework.schedule.trigger.impl;

import java.util.concurrent.TimeUnit;
import com.jfireframework.schedule.task.Timetask;

public class RepeatDelayTrigger extends BaseTrigger
{
    private final long delay;
    
    public RepeatDelayTrigger(Timetask timetask, long delay, TimeUnit unit)
    {
        super(timetask);
        this.delay = unit.toMillis(delay);
        calNext();
    }
    
    @Override
    public long deadline()
    {
        return deadline;
    }
    
    @Override
    public void calNext()
    {
        deadline = System.currentTimeMillis() + delay;
    }
    
}
