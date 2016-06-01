package com.jfireframework.baseutil.collection.buffer;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public interface QueueFactory
{
    public <T> Queue<T> newInstance();
}

class ConcurrentQueueFactory implements QueueFactory
{
    
    @Override
    public <T> Queue<T> newInstance()
    {
        return new ConcurrentLinkedQueue<T>();
    }
    
}
