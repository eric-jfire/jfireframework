package com.jfireframework.baseutil.timer;

import java.util.concurrent.TimeUnit;

public interface Timer extends Runnable
{
    /**
     * 添加一个任务，同时指定该任务的延迟时间和时间单位。返回一个超时实例。
     * 
     * @param task
     * @param delay
     * @param unit
     * @return
     */
    public Timeout addTask(TimeTask task, long delay, TimeUnit unit);
    
    /**
     * 结束这个timer计时器。结束之后该timer不能再被使用
     */
    public void stop();
    
    public void start();
    
    /**
     * 返回当前时间。该时间是当前时间减去类初始化的一个间隔时间。以纳秒表示。
     * 
     * @return
     */
    public long currentTime();
}
