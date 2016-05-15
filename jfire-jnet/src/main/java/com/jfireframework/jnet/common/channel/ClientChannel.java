package com.jfireframework.jnet.common.channel;

import java.util.concurrent.Future;

public interface ClientChannel extends JnetChannel
{
    public void signal(Object obj, long cursor);
    
    public void signalAll(Throwable e, long cursor);
    
    public Future<?> addFuture();
}
