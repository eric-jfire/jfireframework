package com.jfireframework.job.trigger.impl;

import com.jfireframework.job.Job;
import com.jfireframework.job.JobManager;
import com.jfireframework.job.trigger.Trigger;

public abstract class AbstractTrigger implements Trigger
{
    protected volatile long    nextTriggerTime;
    protected Job              job;
    protected volatile boolean removed   = false;
    protected JobManager       jobManager;
    
    public AbstractTrigger(Job job)
    {
        this.job = job;
    }
    
    @Override
    public long nextTriggerTime()
    {
        return nextTriggerTime;
    }
    
    @Override
    public Void call()
    {
        try
        {
            // 如果已经确定被移除,就不执行任务
            if (removed == false)
            {
                job.doJob();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //如果任务不执行下一轮,则不添加回任务列表中
        if (job.nextRound() == false)
        {
            return null;
        }
        calNextTriggerTime();
        jobManager.addTrigger(this);
        return null;
    }
    
    @Override
    public void removeJob()
    {
        removed = true;
    }
    
    @Override
    public boolean removed()
    {
        return removed;
    }
    
    public void setJobManager(JobManager jobManager)
    {
        this.jobManager = jobManager;
    }
}
