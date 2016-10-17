package com.jfireframework.jnet2.server.CompletionHandler.weapon.single.read.async.event;

import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.handler.EventHandler;
import com.jfireframework.jnet2.server.CompletionHandler.weapon.single.read.async.AsyncReadHandler;

public class ReciveHandler implements EventHandler<Message, AsyncReadHandler>
{
    
    @Override
    public int getOrder()
    {
        return 0;
    }
    
    @Override
    public Object handle(AsyncReadHandler readHandler, EventBus eventBus)
    {
        readHandler.asyncHandle();
        return null;
    }
    
    @Override
    public Message interest()
    {
        return Message.recive;
    }
    
}
