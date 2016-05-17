package com.jfireframework.jnet.client;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.LockSupport;

public class ResponseFuture implements Future<Object>
{
    private static final Object  NORESULT  = new Object();
    protected volatile Object    result    = NORESULT;
    protected Thread             ownerThread;
    protected volatile Throwable e;
    public static final boolean  READY     = true;
    public static final boolean  UN_READY  = false;
    protected volatile boolean   dataState = UN_READY;
    
    public ResponseFuture()
    {
        result = NORESULT;
        e = null;
        dataState = UN_READY;
    }
    
    public void ready(Object obj, Throwable e)
    {
        this.result = obj;
        this.e = e;
        dataState = READY;
        LockSupport.unpark(ownerThread);
    }
    
    @Override
    public boolean cancel(boolean mayInterruptIfRunning)
    {
        return false;
    }
    
    @Override
    public boolean isCancelled()
    {
        return false;
    }
    
    @Override
    public boolean isDone()
    {
        return result != NORESULT;
    }
    
    @Override
    public Object get() throws InterruptedException, ExecutionException
    {
        // 这个语句不能放在while内部，否则一旦进入while循环后，设置ownerThread前，读取线程调用了ready方法后，就永远没有线程可以唤醒这个等待了。
        // 这个语句放在这里保证了，如果进入while循环，那么就意味着ready方法中写入dataState还没有调用。而一旦调用，根据hb关系，必然可见ownerThread。必然可以唤醒。就不会出现这个get无人唤醒的情况
        ownerThread = Thread.currentThread();
        while (dataState == UN_READY)
        {
            LockSupport.park();
        }
        if (e != null)
        {
            throw new ExecutionException(e);
        }
        return result;
    }
    
    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
    {
        timeout = unit.toNanos(timeout);
        ownerThread = Thread.currentThread();
        long t0 = System.nanoTime();
        while (dataState == UN_READY && timeout > 0)
        {
            LockSupport.parkNanos(timeout);
            timeout -= System.nanoTime() - t0;
            t0 = System.nanoTime();
        }
        if (dataState == UN_READY)
        {
            throw new TimeoutException("等待时间已到达");
        }
        if (result == NORESULT)
        {
            if (e != null)
            {
                throw new ExecutionException(e);
            }
            throw new TimeoutException("等待时间已到达");
        }
        else
        {
            return result;
        }
    }
    
}
