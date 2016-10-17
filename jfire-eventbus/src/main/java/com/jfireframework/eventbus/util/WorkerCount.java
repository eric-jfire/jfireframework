package com.jfireframework.eventbus.util;

public interface WorkerCount
{
    /**
     * 空闲worker数量+1
     * 
     * @return
     */
    public int addIdle();
    
    /**
     * 空闲worker数量+1
     * 
     * @return
     */
    public int reduceIdle();
    
    /**
     * 当前空闲的worker总数
     * 
     * @return
     */
    public int idleWorkers();
    
    /**
     * worker的总数+1。执行这个方法的时候，空闲的worker数量也会+1
     */
    public void increase();
    
    /**
     * worker的总数-1.执行这个方法对手，空闲的worker数量也会-1
     */
    public void decrease();
    
    /**
     * 系统内的worker总数
     * 
     * @return
     */
    public int totalWorker();
    
}
