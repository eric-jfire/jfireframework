package com.jfireframework.eventbus.eventcontext.impl;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.executor.EventHandlerExecutor;
import com.jfireframework.eventbus.handler.EventHandler;

public class NormalEventContext<T extends Enum<? extends EventConfig>> implements EventContext<T>
{
    protected final EventBus             eventBus;
    protected final EventHandlerExecutor executor;
    protected final EventHandler<T, ?>[] combination;
    protected final Object               eventData;
    protected final T                    event;
    protected volatile boolean           finished = false;
    protected Thread                     owner;
    protected volatile boolean           await    = false;
    protected Throwable                  e;
    protected Object                     result;
    
    public NormalEventContext(Object eventData, T event, EventHandler<T, ?>[] combination, EventHandlerExecutor executor, EventBus eventBus)
    {
        this.eventData = eventData;
        this.event = event;
        this.combination = combination;
        this.executor = executor;
        this.eventBus = eventBus;
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
    public T getEvent()
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
    public EventHandlerExecutor executor()
    {
        return executor;
    }
    
    @Override
    public EventHandler<T, ?>[] combinationHandlers()
    {
        return combination;
    }
}
