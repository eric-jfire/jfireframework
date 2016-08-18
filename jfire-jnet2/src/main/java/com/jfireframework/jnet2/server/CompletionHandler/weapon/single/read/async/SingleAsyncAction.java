package com.jfireframework.jnet2.server.CompletionHandler.weapon.single.read.async;

import com.jfireframework.baseutil.disruptor.AbstractExclusiveEntryAction;

public class SingleAsyncAction extends AbstractExclusiveEntryAction
{
    
    @Override
    public <T> void doJob(T data)
    {
        AsyncReadHandler readHandler = (AsyncReadHandler) data;
        readHandler.asyncHandle();
    }
    
}
