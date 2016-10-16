package com.jfireframework.jnet2.server.CompletionHandler.weapon.single.read.async.event;

import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.handler.EventHandler;
import com.jfireframework.jnet2.server.CompletionHandler.weapon.single.read.async.AsyncReadHandler;

public class ReciveHandler implements EventHandler<Message>
{
    
    @Override
    public int getOrder()
    {
        return 0;
    }
    
    @Override
    public void handle(EventContext eventContext, EventBus eventBus)
    {
        AsyncReadHandler readHandler = (AsyncReadHandler) eventContext.getEventData();
        readHandler.asyncHandle();
    }
    
    @Override
    public Enum<? extends EventConfig<Message>> interest()
    {
        return Message.recive;
    }
    
}
