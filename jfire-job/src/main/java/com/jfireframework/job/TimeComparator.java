package com.jfireframework.job;

import java.util.Comparator;
import com.jfireframework.job.trigger.Trigger;

public class TimeComparator implements Comparator<Trigger>
{
    
    @Override
    public int compare(Trigger o1, Trigger o2)
    {
        return (int) (o1.nextTriggerTime() - o2.nextTriggerTime());
    }
    
}
