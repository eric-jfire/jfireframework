package com.jfireframework.baseutil.autonomydisruptor;

public class SpeedAction extends AutonomyExclusiveEntryAction
{
    
    public SpeedAction(AutonomyRingArray ringArray, long cursor, int maxRetrySum)
    {
        super(ringArray, cursor, maxRetrySum);
    }
    
    @Override
    public <T> void doJob(T data)
    {
        
    }
    
}
