package com.jfireframework.schedule.trigger.impl;

import com.jfireframework.schedule.task.Timetask;
import com.jfireframework.schedule.trigger.Trigger;

public abstract class BaseTrigger implements Trigger
{
    protected volatile boolean canceled = false;
    protected final Timetask   timetask;
    protected volatile long    deadline;
    
    public BaseTrigger(Timetask timetask)
    {
        this.timetask = timetask;
    }
    
    @Override
    public void cancel()
    {
        canceled = true;
    }
    
    @Override
    public boolean isCanceled()
    {
        return canceled;
    }
    
    @Override
    public long deadline()
    {
        return deadline;
    }
    
    @Override
    public Timetask timetask()
    {
        return timetask;
    }
    
}
