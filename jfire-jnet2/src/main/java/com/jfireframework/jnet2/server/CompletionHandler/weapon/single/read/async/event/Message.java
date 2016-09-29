package com.jfireframework.jnet2.server.CompletionHandler.weapon.single.read.async.event;

import com.jfireframework.eventbus.event.Event;
import com.jfireframework.eventbus.event.ParallelLevel;

public enum Message implements Event<Message>
{
    recive;
    
    @Override
    public ParallelLevel parallelLevel()
    {
        return ParallelLevel.PAEALLEL;
    }
    
}
