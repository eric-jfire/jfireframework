package com.jfireframework.jnet.common.channel;

import java.util.concurrent.Future;

public class AsyncClientChannelInfo extends AbstractClientChannelInfo
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
