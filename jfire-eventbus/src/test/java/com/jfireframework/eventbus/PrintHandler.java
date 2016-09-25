package com.jfireframework.eventbus;

import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.event.Event;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.handler.EventHandler;

public class PrintHandler implements EventHandler<Print>
{
    
    @Override
    public int getOrder()
    {
        return 0;
    }
    
    @Override
    public void handle(EventContext event, EventBus eventBus)
    {
        System.out.println("打印:" + event.getEventData());
    }
    
    @Override
    public Enum<? extends Event<Print>> interest()
    {
        return Print.one;
    }
    
}
