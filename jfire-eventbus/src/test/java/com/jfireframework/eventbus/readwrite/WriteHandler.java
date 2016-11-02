package com.jfireframework.eventbus.readwrite;

import java.util.concurrent.locks.LockSupport;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.handler.EventHandler;

public class WriteHandler implements EventHandler<ReadWriteEvent, String>
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
        LockSupport.parkNanos(1000 * 1000 * 1000);
        logger.debug(data);
        LockSupport.parkNanos(1000 * 1000 * 1000);
        return null;
    }
    
    @Override
    public ReadWriteEvent interest()
    {
        return ReadWriteEvent.write;
    }
    
}
