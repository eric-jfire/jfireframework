package com.jfireframework.baseutil.autonomydisruptor.waitstrategy;

import com.jfireframework.baseutil.disruptor.waitstrategy.WaitStrategyStopException;

public abstract class AbstractAutonomyWaitStrategy implements AutonomyWaitStrategy
{
    protected static final int running = 0;
    protected static final int stoped  = 1;
    protected volatile int     state   = running;
    
    @Override
    public void detectStopException()
    {
        if (state == stoped)
        {
            throw WaitStrategyStopException.instance;
        }
    }
    
    @Override
    public void stopRunOrWait()
    {
        state = stoped;
        signallBlockwaiting();
    }
    
}
