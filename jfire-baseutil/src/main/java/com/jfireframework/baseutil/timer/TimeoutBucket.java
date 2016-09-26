package com.jfireframework.baseutil.timer;

import java.util.Queue;

public class TimeoutBucket
{
    private Timeout timeout;
    
    public void addTimeout(Timeout timeout)
    {
        timeout.setNext(this.timeout);
        this.timeout = timeout;
    }
    
    public void out(Queue<Timeout> timeouts)
    {
        while (timeout != null)
        {
            timeouts.add(timeout);
            timeout = timeout.next();
        }
    }
    
    public void expire(TimeoutHandler handler)
    {
        while (timeout != null)
        {
            handler.handle(timeout);
            timeout = timeout.next();
        }
    }
}
