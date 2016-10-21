package com.jfireframework.eventbus.bus;

import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.handler.EventHandler;

public interface EventBus
{
    public void addHandler(EventHandler<?, ?> eventHandler);
    
    public void addWorker();
    
    public void reduceWorker();
    
    public void start();
    
    public void stop();
    
    public void post(EventContext<?> eventContext);
    
    public <T extends Enum<? extends EventConfig>> EventContext<?> post(Object data, T event, Object rowkey);
    
    public <T extends Enum<? extends EventConfig>> EventContext<?> post(Object data, T event);
}
