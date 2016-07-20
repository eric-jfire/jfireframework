package com.jfireframework.context.test.function.event;

import javax.annotation.Resource;
import com.jfireframework.context.event.ApplicationEvent;
import com.jfireframework.context.event.EventHandler;

@Resource
public class HaftHandler2 implements EventHandler
{
    
    @Override
    public void handle(ApplicationEvent event)
    {
        UserPhone myEvent = (UserPhone) event.getData();
        SmsEvent type = (SmsEvent) event.getType();
        if (type == SmsEvent.halt)
        {
            System.out.println("用户:" + myEvent.getPhone() + "停机,这是一个额外通知");
        }
    }
    
    @Override
    public Enum<?>[] type()
    {
        return new Enum<?>[] { SmsEvent.halt };
    }
    
}
