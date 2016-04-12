package com.jfireframework.job.trigger.impl;

import com.jfireframework.job.Job;

/**
 * 简单的间隔重复任务，可以指定任务重复次数
 * 
 * @author 林斌（windfire@zailanghua.com）
 * 
 */
public class SimpleIntervalTrigger extends AbstractTrigger
{
    private long intervamillilSeconds;
    private int  remainTimes = -1;
    
    /**
     * 创造一个间隔时间触发的任务
     * 
     * @param intervalSeconds
     */
    public SimpleIntervalTrigger(Job job, long intervamillilSeconds)
    {
        super(job);
        nextTriggerTime = System.currentTimeMillis();
        this.intervamillilSeconds = intervamillilSeconds;
    }
    
    /**
     * 创造一个间隔时间触发的任务，该任务只能执行指定的次数
     * 
     * @param intervalSeconds
     * @param remainTimes
     */
    public SimpleIntervalTrigger(Job job, long intervamillilSeconds, int remainTimes)
    {
        super(job);
        this.remainTimes = remainTimes;
        this.intervamillilSeconds = intervamillilSeconds;
        nextTriggerTime = System.currentTimeMillis();
    }
    
    @Override
    public void calNextTriggerTime()
    {
        if (remainTimes > 0)
        {
            remainTimes--;
        }
        else if (remainTimes == 0)
        {
            removed = true;
            return;
        }
        nextTriggerTime = System.currentTimeMillis() + intervamillilSeconds;
    }
    
}
