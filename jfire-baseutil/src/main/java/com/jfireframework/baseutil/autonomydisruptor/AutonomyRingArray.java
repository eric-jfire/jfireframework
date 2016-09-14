package com.jfireframework.baseutil.autonomydisruptor;

import com.jfireframework.baseutil.disruptor.ringarray.RingArray;

public interface AutonomyRingArray extends RingArray
{
    public void addAction();
    
    public void removeAction(AutonomyEntryAction action);
}
