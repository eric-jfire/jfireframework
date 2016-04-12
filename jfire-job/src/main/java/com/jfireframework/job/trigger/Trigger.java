package com.jfireframework.job.trigger;

import java.util.concurrent.Callable;

public interface Trigger extends Callable<Void>
{
    /**
     * 计算任务触发器下一次执行任务的时间
     */
    public void calNextTriggerTime();
    
    /**
     * 该任务触发器下一次执行任务的时间
     * 
     * @return
     */
    public long nextTriggerTime();
    
    /**
     * 当前的任务触发器是否需要被移除
     * 
     * @return
     */
    public boolean removed();
    
    /**
     * 移除该任务触发器
     */
    public void removeJob();
    
}
