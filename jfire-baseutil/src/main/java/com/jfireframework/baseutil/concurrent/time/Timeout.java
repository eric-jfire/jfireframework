package com.jfireframework.baseutil.concurrent.time;

public interface Timeout
{
    
    public void invoke();
    
    public Timeout next();
    
    public void setNext(Timeout next);
    
    public Timer timer();
    
    /**
     * 返回该任务的毫秒级延迟时间
     * 
     * @return
     */
    public long deadline();
    
    public boolean isCanceled();
    
    public void cancel();
}
