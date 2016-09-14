package com.jfireframework.baseutil.autonomydisruptor;

import com.jfireframework.baseutil.disruptor.EntryAction;

public interface AutonomyEntryAction extends EntryAction
{
    public void publish(Object data);
}
