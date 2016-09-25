package com.jfireframework.eventbus.eventcontext.impl;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import com.jfireframework.baseutil.concurrent.MPMCQueue;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.event.Event;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.eventcontext.MoreContextInfo;
import com.jfireframework.eventbus.handler.EventHandlerContext;

public class NormalEventContext implements EventContext, MoreContextInfo
{
    protected EventBus                       eventBus;
    protected MPMCQueue<EventContext>        eventQueue;
    protected EventHandlerContext<?>         handlerContext;
    protected final Object                   eventData;
    protected final Enum<? extends Event<?>> event;
    protected volatile boolean               finished = false;
    protected Thread                         owner;
    protected volatile boolean               await    = false;
    protected Throwable                      e;
    protected Object                         result;
    
    public NormalEventContext(Object eventData, Enum<? extends Event<?>> event)
    {
        this.eventData = eventData;
        this.event = event;
    }
    
    @Override
    public void join()
    {
        owner = Thread.currentThread();
        await = true;
        while (finished == false)
        {
            EventContext another = eventQueue.poll();
            if (another != null)
            {
                EventHandlerContext<?> handlerContext = ((MoreContextInfo) another).getEventHandlerContext();
                handlerContext.handle(another, eventBus);
            }
            else
            {
                LockSupport.park();
            }
        }
    }
    
    @Override
    public void await()
    {
        owner = Thread.currentThread();
        await = true;
        while (finished == false)
        {
            LockSupport.park();
        }
    }
    
    @Override
    public void setThrowable(Throwable e)
    {
        this.e = e;
    }
    
    @Override
    public Throwable getThrowable()
    {
        return e;
    }
    
    @Override
    public void signal()
    {
        finished = true;
        if (await)
        {
            LockSupport.unpark(owner);
        }
    }
    
    @Override
    public void await(long mills)
    {
        owner = Thread.currentThread();
        await = true;
        long left = TimeUnit.MILLISECONDS.toNanos(mills);
        while (finished == false)
        {
            long t0 = System.nanoTime();
            LockSupport.parkNanos(left);
            long t1 = System.nanoTime();
            left -= t1 - t0;
            if (left < 1000)
            {
                // 1000纳秒其实非常短，使用循环等待就好了
                for (int i = 0; i < 10000; i++)
                {
                    ;
                }
                break;
            }
        }
    }
    
    @Override
    public boolean isFinished()
    {
        return finished;
    }
    
    @Override
    public Object getEventData()
    {
        return eventData;
    }
    
    @Override
    public Enum<? extends Event<?>> getEvent()
    {
        return event;
    }
    
    @Override
    public void setResult(Object result)
    {
        this.result = result;
    }
    
    @Override
    public Object getResult()
    {
        return result;
    }
    
    @Override
    public EventHandlerContext<?> getEventHandlerContext()
    {
        return handlerContext;
    }
    
    @Override
    public MPMCQueue<EventContext> getEventQueue()
    {
        return eventQueue;
    }
    
    @Override
    public void setMoreInfo(EventHandlerContext<?> eventHandlerContext, EventBus eventBus, MPMCQueue<EventContext> eventQueue)
    {
        this.eventBus = eventBus;
        this.eventQueue = eventQueue;
        handlerContext = eventHandlerContext;
    }
    
    @Override
    public EventBus getEventBus()
    {
        return eventBus;
    }
}
