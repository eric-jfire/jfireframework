package com.jfireframework.jnet.server.CompletionHandler.x.capacity.impl.async2;

import com.jfireframework.baseutil.disruptor.AbstractExclusiveEntryAction;
import com.jfireframework.baseutil.disruptor.Entry;

public class Async2TaskAction extends AbstractExclusiveEntryAction
{
    
    @Override
    public void doJob(Entry entry)
    {
        WeaponAsync2ReadHandler readHandler = (WeaponAsync2ReadHandler) entry.getData();
        readHandler.asyncHandle();
        
    }
    
}
