package com.jfireframework.job;

public interface Job
{
    /**
     * 任务处理内容
     */
    public void doJob();
    
    /**
     * 该任务是否还有下一轮，如果没有的话，则删除该任务相关的触发器
     * 
     * @return
     */
    public boolean nextRound();
}
