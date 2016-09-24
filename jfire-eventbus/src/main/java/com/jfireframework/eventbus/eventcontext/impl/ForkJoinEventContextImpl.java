package com.jfireframework.eventbus.eventcontext.impl;

import com.jfireframework.baseutil.concurrent.MPMCQueue;
import com.jfireframework.eventbus.event.Event;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.eventcontext.ForkJoinEventContext;

public class ForkJoinEventContextImpl implements ForkJoinEventContext
{
    protected final MPMCQueue<EventContext> eventQueue;
    
    public ForkJoinEventContextImpl(MPMCQueue<EventContext> eventQueue)
    {
        this.eventQueue = eventQueue;
    }
    
    @Override
    public Object rowkey()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public void await()
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void setThrowable(Throwable e)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public Throwable getThrowable()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public void setResult(Object result)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public Object getResult()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public void signal()
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void await(long mills)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public boolean isFinished()
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public Object getEventData()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Enum<? extends Event<?>> getEvent()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
}
