package com.jfireframework.eventbus;

import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.handler.EventHandler;

public class RowPrint implements EventHandler<Print, String>
{
    private long t0;
    
    @Override
    public int getOrder()
    {
        return 0;
    }
    
    @Override
    public Object handle(String data, EventBus eventBus)
    {
        String value = data;
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
        return null;
    }
    
    @Override
    public Print interest()
    {
        return Print.single;
    }
    
}
