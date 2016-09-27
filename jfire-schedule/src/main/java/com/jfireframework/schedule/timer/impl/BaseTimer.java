package com.jfireframework.schedule.timer.impl;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import com.jfireframework.baseutil.concurrent.MPSCQueue;
import com.jfireframework.baseutil.concurrent.UnsafeIntFieldUpdater;
import com.jfireframework.schedule.timer.ExpireHandler;
import com.jfireframework.schedule.timer.Timer;
import com.jfireframework.schedule.trigger.Trigger;

public abstract class BaseTimer implements Timer
{
    protected final MPSCQueue<Trigger>                      waitForAddTriggers = new MPSCQueue<Trigger>();
    // 纳秒表示的tick间隔
    protected final long                                    tickDuration;
    protected volatile int                                  state              = NOT_START;
    public static final int                                 NOT_START          = 0;
    public static final int                                 STARTED            = 1;
    public static final int                                 termination        = -1;
    protected static final UnsafeIntFieldUpdater<BaseTimer> state_updater      = new UnsafeIntFieldUpdater<BaseTimer>(BaseTimer.class, "state");
    private final long                                      startTime          = System.nanoTime();
    protected final ExpireHandler                           expireHandler;
    
    public BaseTimer(long tickDuration, TimeUnit unit, ExpireHandler expireHandler)
    {
        this.tickDuration = unit.toNanos(tickDuration);
        this.expireHandler = expireHandler;
    }
    
    /**
     * 要使用这种方式等待的原因很简单。因为系统的等待并不是完美的按照约定的时间进行，有可能会比我们的参数时间要多一些。
     * 而且其余的代码执行也会有时间上的损耗。所以每次都要计算是否确实可以等待
     */
    protected void waitToNextTick(long tick)
    {
        long deadline = (tick + 1) * tickDuration;
        for (;;)
        {
            final long currentTime = currentTime();
            long sleedTimeNano = deadline - currentTime;
            if (sleedTimeNano < 0)
            {
                return;
            }
            if (sleedTimeNano < 1000)
            {
                for (int i = 0; i < 1000; i++)
                {
                    ;
                }
            }
            else
            {
                LockSupport.parkNanos(sleedTimeNano);
            }
        }
    }
    
    @Override
    public void stop()
    {
        state = termination;
    }
    
    /**
     * 返回距离timer启动时间的纳秒间隔。
     * 
     * @return
     */
    @Override
    public long currentTime()
    {
        return System.nanoTime() - startTime;
    }
    
    @Override
    public void add(Trigger trigger)
    {
        trigger.setTimer(this);
        waitForAddTriggers.offer(trigger);
    }
}
