package com.jfireframework.eventbus.eventthread;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicIntergerIdleCount implements IdleCount
{
    private final AtomicInteger count = new AtomicInteger();
    
    @Override
    public int add()
    {
        return count.incrementAndGet();
    }
    
    @Override
    public int reduce()
    {
        return count.decrementAndGet();
    }
    
    @Override
    public int nowIdleCount()
    {
        return count.get();
    }
    
}
