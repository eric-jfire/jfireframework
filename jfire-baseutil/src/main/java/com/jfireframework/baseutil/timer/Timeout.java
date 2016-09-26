package com.jfireframework.baseutil.timer;

public interface Timeout
{
    
    public void invoke();
    
    public Timeout next();
    
    public void setNext(Timeout next);
    
    public Timer timer();
    
    /**
     * 返回该任务距离timer的启动时间的距离纳秒时间
     * 
     * @return
     */
    public long deadline();
    
    public boolean isCanceled();
    
    public void cancel();
}
