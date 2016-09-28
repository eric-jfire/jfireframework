package com.jfireframework.schedule.trigger;

import com.jfireframework.schedule.task.Timetask;

public interface Trigger
{
    
    public Timetask timetask();
    
    /**
     * 类似与System.currentTimeMillis()的定义。表示下一次触发的时间
     * 
     * @return
     */
    public long deadline();
    
    public void calNextline();
}
