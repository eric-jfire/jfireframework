package com.jfireframework.eventbus.event;

public interface Event<T>
{
    /**
     * 事件类型
     * 
     * @return
     */
    public Enum<?> type();
    
    /**
     * 事件是否串行处理
     * 
     * @return
     */
    public boolean serial();
    
    /**
     * 事件示例
     * 
     * @return
     */
    public T instance();
}
