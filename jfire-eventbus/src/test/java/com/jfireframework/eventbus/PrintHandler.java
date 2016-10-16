package com.jfireframework.eventbus;

import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.handler.EventHandler;

public class PrintHandler implements EventHandler<Print, String>
{
    
    @Override
    public int getOrder()
    {
        return 0;
    }
    
    @Override
    public Object handle(String data, EventBus eventBus)
    {
        System.out.println("打印:" + data);
        return null;
    }
    
    @Override
    public Print interest()
    {
        return Print.one;
    }
    
}
