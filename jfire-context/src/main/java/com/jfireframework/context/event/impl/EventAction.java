package com.jfireframework.context.event.impl;

import java.util.IdentityHashMap;
import com.jfireframework.baseutil.disruptor.AbstractExclusiveEntryAction;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.context.event.ApplicationEvent;
import com.jfireframework.context.event.EventHandler;

public class EventAction extends AbstractExclusiveEntryAction
{
    private final IdentityHashMap<Class<?>, Integer> map;
    private final EventHandler[][][]                 handlers;
    private final static Logger                      logger = ConsoleLogFactory.getLogger();
    
    public EventAction(EventHandler[][][] handlers, IdentityHashMap<Class<?>, Integer> map)
    {
        this.handlers = handlers;
        this.map = map;
    }
    
    @Override
    public <T> void doJob(T data)
    {
        try
        {
            ApplicationEvent event = (ApplicationEvent) data;
            int sequence = map.get(event.getType().getClass());
            int index = event.getType().ordinal();
            for (EventHandler each : handlers[sequence][index])
            {
                each.handle(event);
            }
            event.signal();
        }
        catch (Exception e)
        {
            logger.error("异步事件处理异常", e);
        }
    }
    
}
