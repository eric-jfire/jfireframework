package com.jfireframework.jnet.client;

import java.util.concurrent.Future;

public class FutureClient extends AioClient
{
    
    public Future<?> buildFuture()
    {
        return clientChannelInfo.addFuture();
    }
    
}
