package com.jfireframework.schedule.trigger.impl;

import java.util.Calendar;
import com.jfireframework.schedule.task.Timetask;

public class FixDayTimeTrigger extends BaseTrigger
{
    private final int hour;
    private final int minute;
    private final int second;
    
    public FixDayTimeTrigger(Timetask timetask, int hour, int minute, int second)
    {
        super(timetask);
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        calNext();
    }
    
    @Override
    public void calNext()
    {
        Calendar now = Calendar.getInstance();
        Calendar target = Calendar.getInstance();
        target.set(Calendar.HOUR_OF_DAY, hour);
        target.set(Calendar.MINUTE, minute);
        target.set(Calendar.SECOND, second);
        if (target.after(now) == false)
        {
            target.add(Calendar.DAY_OF_YEAR, 1);
        }
        else
        {
            ;
        }
        deadline = target.getTimeInMillis();
    }
    
}
