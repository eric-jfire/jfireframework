package com.jfireframework.baseutil.concurrent.time;

public class DefaultTimeoutHandler implements TimeoutHandler
{
    
    @Override
    public void handle(Timeout timeout)
    {
        timeout.invoke();
    }
    
}
