package com.jfireframework.jnet.common.channel.impl;

import java.util.concurrent.Future;

public class AsyncClientChannelInfo extends AbstractClientChannel
{
    public Future<?> addFuture()
    {
        return NORESULT;
    }
    
    public void signal(Object obj, long cursor)
    {
    }
    
    public void signalAll(Throwable e, long cursor)
    {
    }
}
