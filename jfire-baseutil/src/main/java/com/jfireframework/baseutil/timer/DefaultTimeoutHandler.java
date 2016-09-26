package com.jfireframework.baseutil.timer;

public class DefaultTimeoutHandler implements TimeoutHandler
{
    
    @Override
    public void handle(Timeout timeout)
    {
        timeout.invoke();
    }
    
}
