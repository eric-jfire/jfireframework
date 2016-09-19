package com.jfireframework.baseutil.autonomydisruptor;

import com.jfireframework.baseutil.concurrent.CpuCachePadingInt;
import com.jfireframework.baseutil.disruptor.ringarray.RingArray;
import sun.util.logging.resources.logging;

public interface AutonomyRingArray extends RingArray
{
    public CpuCachePadingInt idleCount();
    
    public boolean addAction();
    
    public long getMax();
    
    public boolean removeAction(AutonomyEntryAction action);
}
