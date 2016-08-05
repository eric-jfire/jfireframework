package com.jfireframework.jnet.common.channel.impl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import com.jfireframework.jnet.common.channel.ClientChannel;

public class AsyncClientChannelInfo extends AbstractChannel implements ClientChannel
{
    
    public static Future<Void> NORESULT = new Future<Void>() {
        
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
            return true;
        }
        
        @Override
        public Void get() throws InterruptedException, ExecutionException
        {
            return null;
        }
        
        @Override
        public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
        {
            return null;
        }
    };
    
    public Future<?> addFuture()
    {
        return NORESULT;
    }
    
    public void signal(Object obj)
    {
    }
    
    public void signalAll(Throwable e)
    {
    }
}
