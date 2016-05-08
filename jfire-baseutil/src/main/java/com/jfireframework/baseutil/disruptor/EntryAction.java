package com.jfireframework.baseutil.disruptor;

import com.jfireframework.baseutil.disruptor.ringarray.RingArray;

public interface EntryAction extends Runnable
{
    public long cursor();
    
    public void setRingArray(RingArray ringArray);
    
}
