package com.jfireframework.baseutil.concurrent.time;

import java.util.LinkedList;
import java.util.List;

public class TimeoutBucket
{
    private List<Timeout> timeouts = new LinkedList<Timeout>();
    
    public void addTimeout(Timeout timeout)
    {
        timeouts.add(timeout);
    }
    
    public List<Timeout> getAll()
    {
        return timeouts;
    }
    
    public void clear()
    {
        timeouts.clear();
    }
    
    public void expire(TimeoutHandler handler)
    {
        if (timeouts != null)
        {
            for (Timeout each : timeouts)
            {
                if (each.isCanceled() == false)
                {
                    handler.handle(each);
                }
            }
            timeouts.clear();
        }
    }
}
