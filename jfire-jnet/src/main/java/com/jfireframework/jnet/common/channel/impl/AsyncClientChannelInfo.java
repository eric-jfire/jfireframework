package com.jfireframework.jnet.common.channel.impl;

import java.util.concurrent.Future;

public class AsyncClientChannelInfo extends AbstractClientChannel
{
    public Future<?> addFuture()
    {
        return NORESULT;
    }
    
    public void signal(Object obj)
    {
    }
    
    public void signalAll(Throwable e)
    {
    }
}
