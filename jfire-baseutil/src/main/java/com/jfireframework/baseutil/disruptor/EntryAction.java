package com.jfireframework.baseutil.disruptor;

public interface EntryAction extends Runnable
{
    public long cursor();
    
    public void setDisruptor(Disruptor disruptor);
    
    public void publish(Object data);
}
