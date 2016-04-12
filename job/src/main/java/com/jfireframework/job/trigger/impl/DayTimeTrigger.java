package com.jfireframework.job.trigger.impl;

import java.util.Calendar;
import com.jfireframework.job.Job;

/**
 * 定点时间触发器，在每天的一个固定时间触发
 * 
 * @author 林斌（windfire@zailanghua.com）
 * 
 */
public class DayTimeTrigger extends AbstractTrigger
{
    private int      hour;
    private int      minute;
    private int      second;
    private Calendar calendar = Calendar.getInstance();
    
    /**
     * 初始化一个定点任务触发器，使用小时，分钟，秒进行初始化
     * 
     * @param hour
     * @param minute
     * @param second
     */
    public DayTimeTrigger(Job job, int hour, int minute, int second)
    {
        super(job);
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        calNextTriggerTime();
    }
    
    @Override
    public void calNextTriggerTime()
    {
        if (job.nextRound() == false)
        {
            removed = true;
            return;
        }
        long now = System.currentTimeMillis();
        calendar.setTimeInMillis(now);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        /**
         * 如果任务很快完成，消耗时间不足1秒。必须将毫秒设置为0，这样才可以保证计算出来的毫秒值小于now
         */
        calendar.set(Calendar.MILLISECOND, 0);
        while (calendar.getTimeInMillis() < now)
        {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        nextTriggerTime = calendar.getTime().getTime();
    }
    
}
