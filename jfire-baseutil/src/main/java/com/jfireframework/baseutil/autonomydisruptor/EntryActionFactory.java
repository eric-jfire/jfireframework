package com.jfireframework.baseutil.autonomydisruptor;

public interface EntryActionFactory
{
    public AutonomyEntryAction newEntryAction(AutonomyRingArray ringArray, long cursor);
}
