package com.jfireframework.eventbus.eventthread;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicIntergerIdleCount implements IdleCount
{
    private final AtomicInteger count = new AtomicInteger();
    
    @Override
    public void add()
    {
        count.incrementAndGet();
    }
    
    @Override
    public void reduce()
    {
        count.decrementAndGet();
    }
    
    @Override
    public int nowIdleCount()
    {
        return count.get();
    }
    
}
