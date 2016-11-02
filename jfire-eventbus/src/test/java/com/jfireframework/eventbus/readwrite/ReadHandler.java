package com.jfireframework.eventbus.readwrite;

import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.handler.EventHandler;

public class ReadHandler implements EventHandler<ReadWriteEvent, String>
{
    private Logger logger = ConsoleLogFactory.getLogger();
    
    @Override
    public int getOrder()
    {
        return 0;
    }
    
    @Override
    public Object handle(String data, EventBus eventBus)
    {
        logger.debug(data);
        return null;
    }
    
    @Override
    public ReadWriteEvent interest()
    {
        return ReadWriteEvent.read;
    }
    
}
