package com.jfireframework.context.event.impl;

import javax.annotation.PostConstruct;
import com.jfireframework.eventbus.bus.impl.CalculateEventBus;
import com.jfireframework.eventbus.handler.EventHandler;

public class CalculateEventPost extends AbstractEventPoster
{
    private int coreWorker = Runtime.getRuntime().availableProcessors();
    
    @PostConstruct
    public void init()
    {
        eventBus = new CalculateEventBus(coreWorker);
        for (EventHandler<?, ?> each : handlers)
        {
            eventBus.addHandler(each);
        }
        eventBus.start();
    }
}
