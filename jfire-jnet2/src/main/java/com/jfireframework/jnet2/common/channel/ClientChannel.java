package com.jfireframework.jnet2.common.channel;

import java.util.concurrent.Future;

public interface ClientChannel extends JnetChannel
{
    public void signal(Object obj);
    
    public void signalAll(Throwable e);
    
    public Future<?> addFuture();
}
