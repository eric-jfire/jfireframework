package com.jfireframework.eventbus.util;

public interface IdleCount
{
    public int add();
    
    public int reduce();
    
    public int nowIdleCount();
}
