package com.jfireframework.eventbus;

import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.handler.EventHandler;

public class SpeedHandler implements EventHandler<Speed, String>
{
    
    @Override
    public int getOrder()
    {
        // TODO Auto-generated method stub
        return 0;
    }
    
    @Override
    public Object handle(String value, EventBus eventBus)
    {
        // PoolContext poolContext = (PoolContext) eventContext.getEventData();
        // String value = poolContext.poll();
        // eventContext.setResult(value);
        return value;
    }
    
    @Override
    public Speed interest()
    {
        return Speed.speed;
    }
}
