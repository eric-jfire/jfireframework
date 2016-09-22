package com.jfireframework.eventbus.event;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class ApplicationEvent implements RowId
{
    private final Object                   eventData;
    private final Enum<? extends Event<?>> event;
    private final int                      rowId;
    private volatile boolean               finished = false;
    private Thread                         owner;
    private volatile boolean               await    = false;
    private Throwable                      e;
    
    public ApplicationEvent(Object eventData, Enum<? extends Event<?>> event, int rowId)
    {
        this.eventData = eventData;
        this.event = event;
        this.rowId = rowId;
    }
    
    /**
     * 等待直到该事件被处理完成
     */
    public void await()
    {
        owner = Thread.currentThread();
        await = true;
        while (finished == false)
        {
            LockSupport.park();
        }
    }
    
    public void setThrowable(Throwable e)
    {
        this.e = e;
    }
    
    public Throwable getThrowable()
    {
        return e;
    }
    
    /**
     * }
     * 完成该事件，唤醒等待该事件的线程
     */
    public void signal()
    {
        finished = true;
        if (await)
        {
            LockSupport.unpark(owner);
        }
    }
    
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
            // 1000纳秒其实非常短，就不要再等待了
            if (left < 1000)
            {
                break;
            }
        }
    }
    
    public boolean isFinished()
    {
        return finished;
    }
    
    public Object getEventData()
    {
        return eventData;
    }
    
    public Enum<? extends Event<?>> getEvent()
    {
        return event;
    }
    
    @Override
    public int id()
    {
        return rowId;
    }
}
