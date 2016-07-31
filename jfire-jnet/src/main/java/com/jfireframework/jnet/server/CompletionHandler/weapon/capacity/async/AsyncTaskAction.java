package com.jfireframework.jnet.server.CompletionHandler.weapon.capacity.async;

import com.jfireframework.baseutil.disruptor.AbstractExclusiveEntryAction;
import com.jfireframework.baseutil.disruptor.Entry;

public class AsyncTaskAction extends AbstractExclusiveEntryAction
{
    
    @Override
    public void doJob(Entry entry)
    {
        WeaponAsyncReadHandler readHandler = (WeaponAsyncReadHandler) entry.getData();
        readHandler.asyncHandle();
    }
    
}
