package com.jfireframework.schedule.timer;

import com.jfireframework.schedule.trigger.Trigger;

public interface Timer extends Runnable
{
    public void add(Trigger trigger);
    
    /**
     * 返回当前时间。该时间是当前时间减去类初始化的一个间隔时间。以纳秒表示。
     * 
     * @return
     */
    public long currentTime();
    
    /**
     * 结束这个timer计时器。结束之后该timer不能再被使用
     */
    public void stop();
    
}
