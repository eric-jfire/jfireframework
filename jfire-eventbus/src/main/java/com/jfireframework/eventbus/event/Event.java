package com.jfireframework.eventbus.event;

public interface Event<T>
{
    
    public EventType type();
    
    /**
     * 事件示例
     * 
     * @return
     */
    public T instance();
}
