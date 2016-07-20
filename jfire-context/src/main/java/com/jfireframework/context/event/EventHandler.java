package com.jfireframework.context.event;

public interface EventHandler
{
    /**
     * 处理一个事件
     * 
     * @param event
     */
    public void handle(ApplicationEvent event);
    
    /**
     * 该处理器感兴趣的事件
     * 
     * @return
     */
    public Enum<?>[] type();
}
