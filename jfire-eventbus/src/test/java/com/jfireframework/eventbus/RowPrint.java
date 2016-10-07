package com.jfireframework.eventbus;

import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.event.Event;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.handler.EventHandler;

public class RowPrint implements EventHandler<Print>
{
    private long t0;
    
    @Override
    public int getOrder()
    {
        return 0;
    }
    
    @Override
    public void handle(EventContext eventContext, EventBus eventBus)
    {
        String value = (String) eventContext.getEventData();
        if (t0 == 0)
        {
            t0 = System.currentTimeMillis();
        }
        System.out.println(Thread.currentThread().getName() + "打印：" + value + "," + (System.currentTimeMillis() - t0));
        t0 = System.currentTimeMillis();
        try
        {
            Thread.sleep(5000);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    @Override
    public Enum<? extends Event<Print>> interest()
    {
        return Print.single;
    }
    
}
