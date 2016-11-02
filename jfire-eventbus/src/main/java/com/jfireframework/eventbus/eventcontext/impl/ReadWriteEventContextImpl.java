package com.jfireframework.eventbus.eventcontext.impl;

import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.eventcontext.ReadWriteEventContext;
import com.jfireframework.eventbus.executor.EventHandlerExecutor;
import com.jfireframework.eventbus.handler.EventHandler;

public class ReadWriteEventContextImpl<T extends Enum<? extends EventConfig>> extends NormalEventContext<T> implements ReadWriteEventContext<T>
{
    private boolean   immediateMode = false;
    private final int mode;
    
    public ReadWriteEventContextImpl(int mode, Object eventData, Enum<? extends EventConfig> event, EventHandler<?, ?>[] combination, EventHandlerExecutor executor, EventBus eventBus)
    {
        super(eventData, event, combination, executor, eventBus);
        this.mode = mode;
    }
    
    @Override
    public boolean immediateInvoke()
    {
        return immediateMode;
    }
    
    @Override
    public int mode()
    {
        return mode;
    }
    
    @Override
    public void setImmediateMode()
    {
        immediateMode = true;
    }
    
}
