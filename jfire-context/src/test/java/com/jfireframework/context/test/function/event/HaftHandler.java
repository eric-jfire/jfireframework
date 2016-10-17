package com.jfireframework.context.test.function.event;

import javax.annotation.Resource;
import com.jfireframework.context.ContextInitFinish;
import com.jfireframework.context.event.EventPoster;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.handler.EventHandler;

@Resource
public class HaftHandler implements EventHandler<SmsEvent, UserPhone>, ContextInitFinish
{
    @Resource
    private EventPoster publisher;
    
    @Override
    public Object handle(UserPhone myEvent, EventBus eventBus)
    {
        System.out.println("asdasd");
        System.out.println("用户:" + myEvent.getPhone() + "欠费");
        return null;
    }
    
    @Override
    public int getOrder()
    {
        return 0;
    }
    
    @Override
    public void afterContextInit()
    {
        UserPhone phone = new UserPhone();
        phone.setPhone("1775032");
        publisher.post(phone, SmsEvent.halt).await();
    }
    
    @Override
    public SmsEvent interest()
    {
        return SmsEvent.halt;
    }
    
}
