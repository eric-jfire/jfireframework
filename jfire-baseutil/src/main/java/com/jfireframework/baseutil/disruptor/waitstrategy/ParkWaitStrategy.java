package com.jfireframework.baseutil.disruptor.waitstrategy;

import java.util.concurrent.locks.LockSupport;
import com.jfireframework.baseutil.concurrent.CpuCachePadingInt;
import com.jfireframework.baseutil.disruptor.ringarray.RingArray;

public class ParkWaitStrategy extends AbstractWaitStrategy
{
    
    private final Thread[]          threads;
    private final static int        WORK     = 0;
    private final static int        PARKED   = 1;
    private final CpuCachePadingInt parkFlag = new CpuCachePadingInt(WORK);
    
    public ParkWaitStrategy(Thread[] threads)
    {
        this.threads = threads;
    }
    
    class holder
    {
        private int  state;
        private long next;
        
        public holder(int state, long next)
        {
            this.state = state;
            this.next = next;
        }
    }
    
    @Override
    public void waitFor(long next, RingArray ringArray) throws WaitStrategyStopException
    {
        while (ringArray.isAvailable(next) == false)
        {
            int tmp = parkFlag.value();
            if (tmp== WORK)
            {
                if (parkFlag.compareAndSwap(WORK, PARKED))
                {
                    if (ringArray.isAvailable(next))
                    {
                        parkFlag.set(WORK);
                        for (Thread each : threads)
                        {
                            LockSupport.unpark(each);
                        }
                        break;
                    }
                }
            }
            LockSupport.park(new holder(tmp, next));
            detectStopException();
        }
    }
    
    @Override
    public void signallBlockwaiting()
    {
        /**
         * 如果所有的线程在同一个时间内调度这个if。首先通过第一个状态判断。
         * 然后执行第二个判断的时候，等待线程执行上
         */
        if (parkFlag.value() == PARKED && parkFlag.compareAndSwap(PARKED, WORK))
        {
            for (Thread each : threads)
            {
                LockSupport.unpark(each);
            }
        }
        
    }
    
}
