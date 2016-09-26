package com.jfireframework.baseutil.timer;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import com.jfireframework.baseutil.concurrent.MPSCLinkedQueue;
import com.jfireframework.baseutil.concurrent.UnsafeIntFieldUpdater;

public abstract class AbstractTimer implements Timer
{
    protected MPSCLinkedQueue<Timeout>                          timeouts      = new MPSCLinkedQueue<Timeout>();
    // 纳秒表示的tick间隔
    protected final long                                        tickDuration;
    protected final TimeoutHandler                              handler;
    protected volatile boolean                                  stop          = false;
    protected volatile int                                      state         = NOT_START;
    private static final int                                    NOT_START     = 0;
    private static final int                                    STARTED       = 1;
    protected static final UnsafeIntFieldUpdater<AbstractTimer> state_updater = new UnsafeIntFieldUpdater<AbstractTimer>(AbstractTimer.class, "state");
    private long                                                startTime;
    
    public AbstractTimer(long tickDuration, TimeUnit unit, TimeoutHandler handler)
    {
        this.tickDuration = unit.toNanos(tickDuration);
        this.handler = handler;
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
            LockSupport.parkNanos(sleedTimeNano);
        }
    }
    
    @Override
    public void stop()
    {
        stop = true;
    }
    
    @Override
    public void start()
    {
        if (state == NOT_START)
        {
            if (state_updater.compareAndSwap(this, NOT_START, STARTED))
            {
                startTime = System.nanoTime();
                new Thread(this).start();
            }
        }
    }
    
    /**
     * 返回距离timer启动时间的纳秒间隔。
     * 
     * @return
     */
    protected long currentTime()
    {
        return System.nanoTime() - startTime;
    }
}
