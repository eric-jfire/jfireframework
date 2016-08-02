package com.jfireframework.baseutil.disruptor;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;

public class TestAction extends AbstractExclusiveEntryAction
{
    
    @Override
    public <T> void doJob(T data)
    {
        ByteBuf<?> buf = (ByteBuf<?>) data;
        buf.readIndex(0);
    }
    
}
