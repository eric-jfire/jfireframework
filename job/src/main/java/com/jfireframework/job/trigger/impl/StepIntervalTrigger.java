package com.jfireframework.job.trigger.impl;

import com.jfireframework.job.Job;

public class StepIntervalTrigger extends AbstractTrigger
{
    private long totalInterval;
    private long stepInterval;
    private long interval = 0;
    
    public StepIntervalTrigger(Job job, long totalInterval, int step)
    {
        super(job);
        this.totalInterval = totalInterval;
        stepInterval = totalInterval / step;
        nextTriggerTime = System.currentTimeMillis();
    }
    
    @Override
    public void calNextTriggerTime()
    {
        if (interval < totalInterval)
        {
            interval += stepInterval;
        }
        nextTriggerTime = System.currentTimeMillis() + interval;
    }
    
}
