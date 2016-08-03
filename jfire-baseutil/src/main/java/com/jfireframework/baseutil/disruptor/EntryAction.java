package com.jfireframework.baseutil.disruptor;

import com.jfireframework.baseutil.disruptor.ringarray.RingArray;

public interface EntryAction extends Runnable
{
    /**
     * 处理器当前可以处理的序号。初始值是0
     * 
     * @return
     */
    public long cursor();
    
    public void setRingArray(RingArray ringArray);
    
}
