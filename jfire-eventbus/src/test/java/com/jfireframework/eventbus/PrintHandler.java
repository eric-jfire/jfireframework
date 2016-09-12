package com.jfireframework.eventbus;

import com.jfireframework.eventbus.event.ApplicationEvent;
import com.jfireframework.eventbus.event.Event;
import com.jfireframework.eventbus.handler.EventHandler;

public class PrintHandler implements EventHandler<Print>
{
    
    @Override
    public int getOrder()
    {
        return 0;
    }
    
    @Override
    public void handle(ApplicationEvent event)
    {
        System.out.println("打印");
    }
    
    @Override
    public Enum<? extends Event<Print>> interest()
    {
        return Print.one;
    }
    
}
