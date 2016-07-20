package com.jfireframework.context.test.function.event;

import javax.annotation.Resource;
import com.jfireframework.context.ContextInitFinish;
import com.jfireframework.context.event.ApplicationEvent;
import com.jfireframework.context.event.EventHandler;
import com.jfireframework.context.event.EventPublisher;

@Resource
public class HaftHandler implements EventHandler, ContextInitFinish
{
    @Resource
    private EventPublisher  publisher;
    private final Enum<?>[] interest = new Enum<?>[] { SmsEvent.Arrearage, SmsEvent.halt };
    
    @Override
    public Enum<?>[] type()
    {
        return interest;
    }
    
    @Override
    public void handle(ApplicationEvent event)
    {
        UserPhone myEvent = (UserPhone) event.getData();
        SmsEvent type = (SmsEvent) event.getType();
        if (type == SmsEvent.halt)
        {
            System.out.println("用户:" + myEvent.getPhone() + "停机");
        }
        else if (type == SmsEvent.Arrearage)
        {
            System.out.println("用户:" + myEvent.getPhone() + "欠费");
        }
    }
    
    @Override
    public int getOrder()
    {
        // TODO Auto-generated method stub
        return 0;
    }
    
    @Override
    public void afterContextInit()
    {
        UserPhone phone = new UserPhone();
        phone.setPhone("1775032");
        ApplicationEvent event = new ApplicationEvent();
        event.setData(phone);
        event.setType(SmsEvent.halt);
        publisher.publish(event);
    }
    
}
