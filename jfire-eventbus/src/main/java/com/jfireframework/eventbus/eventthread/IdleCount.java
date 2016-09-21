package com.jfireframework.eventbus.eventthread;

public interface IdleCount
{
    public int add();
    
    public int reduce();
    
    public int nowIdleCount();
}
