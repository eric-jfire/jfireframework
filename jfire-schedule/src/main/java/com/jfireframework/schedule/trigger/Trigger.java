package com.jfireframework.schedule.trigger;

import com.jfireframework.schedule.task.Timetask;

public interface Trigger
{
    
    /**
     * 取消该触发器
     */
    public void cancel();
    
    /**
     * 该触发器是否已经取消
     * 
     * @return
     */
    public boolean isCanceled();
    
    public Timetask timetask();
    
    /**
     * 类似与System.currentTimeMillis()的定义。表示下一次触发的时间.如果为负数。意味着该触发器已经结束生命周期
     * 
     * @return
     */
    public long deadline();
    
    public void calNext();
}
