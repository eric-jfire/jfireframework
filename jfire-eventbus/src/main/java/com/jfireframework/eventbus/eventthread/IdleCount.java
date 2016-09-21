package com.jfireframework.eventbus.eventthread;

public interface IdleCount
{
    public void add();
    
    public void reduce();
    
    public int nowIdleCount();
}
