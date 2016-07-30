package com.jfireframework.jnet.server.CompletionHandler.x.capacity.impl.async;

import com.jfireframework.baseutil.disruptor.AbstractExclusiveEntryAction;
import com.jfireframework.baseutil.disruptor.Entry;
import com.jfireframework.jnet.server.CompletionHandler.x.capacity.WeaponWriteHandler;
import com.jfireframework.jnet.server.CompletionHandler.x.capacity.impl.sync.WeaponSyncWriteHandler;

public class AsyncTaskAction extends AbstractExclusiveEntryAction
{
    
    @Override
    public void doJob(Entry entry)
    {
        WeaponAsyncReadHandler readHandler = (WeaponAsyncReadHandler) entry.getData();
        WeaponSyncWriteHandler writeHandler = readHandler.writeHandler();
        if (writeHandler.available())
        {
            
        }
        else
        {
            readHandler.endAsyncTryPublish();
        }
    }
    
}
