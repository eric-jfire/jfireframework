package com.jfireframework.schedule.task;

public interface Timetask
{
    public void cancel();
    
    public boolean isCanceled();
    
    public void invoke();
}
