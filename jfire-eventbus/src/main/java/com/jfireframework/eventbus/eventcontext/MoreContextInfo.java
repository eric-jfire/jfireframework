package com.jfireframework.eventbus.eventcontext;

import com.jfireframework.baseutil.concurrent.MPMCQueue;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.handler.EventHandlerContext;

public interface MoreContextInfo
{
    
    public void setMoreInfo(EventHandlerContext<?> eventHandlerContext, EventBus eventBus, MPMCQueue<EventContext> eventQueue);
    
    public MPMCQueue<EventContext> getEventQueue();
    
    public EventBus getEventBus();
    
    public EventHandlerContext<?> getEventHandlerContext();
}
