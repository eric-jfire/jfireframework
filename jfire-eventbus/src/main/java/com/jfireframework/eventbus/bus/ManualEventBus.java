package com.jfireframework.eventbus.bus;

public interface ManualEventBus extends EventBus
{
    /**
     * 新创建一个线程资源
     */
    public void createResource();
    
    /**
     * 回收一个线程资源
     */
    public void recycleResource();
}
