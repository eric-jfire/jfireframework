package com.jfireframework.schedule.trigger;

import com.jfireframework.schedule.task.Timetask;
import com.jfireframework.schedule.timer.Timer;

public interface Trigger
{
    
    public void setTimer(Timer timer);
    
    public Timer getTimer();
    
    public Timetask timetask();
    
    /**
     * 类似与System.currentTimeMillis()的定义。表示下一次触发的时间
     * 
     * @return
     */
    public long deadline();
}
