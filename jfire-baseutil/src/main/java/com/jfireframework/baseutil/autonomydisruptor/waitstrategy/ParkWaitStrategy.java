package com.jfireframework.baseutil.autonomydisruptor.waitstrategy;

import java.util.concurrent.locks.LockSupport;
import com.jfireframework.baseutil.disruptor.ringarray.RingArray;
import com.jfireframework.baseutil.disruptor.waitstrategy.WaitStrategyStopException;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import sun.misc.Unsafe;

public class ParkWaitStrategy extends AbstractAutonomyWaitStrategy
{
    
    private volatile Waiter       waiterHead = new Waiter(null);
    protected static final Unsafe unsafe     = ReflectUtil.getUnsafe();
    private static final long     offset     = ReflectUtil.getFieldOffset("waiterHead", ParkWaitStrategy.class);
    
    class Waiter
    {
        private final Thread thread;
        private Waiter       next;
        
        public Waiter(Thread thread)
        {
            this.thread = thread;
        }
    }
    
    @Override
    public void waitFor(long next, RingArray ringArray) throws WaitStrategyStopException
    {
        boolean recheck = false;
        while (ringArray.isAvailable(next) == false)
        {
            if (recheck == false)
            {
                enqueue();
                recheck = true;
                continue;
            }
            LockSupport.park();
            recheck = false;
            detectStopException();
        }
    }
    
    private Waiter enqueue()
    {
        Waiter newWaiter = new Waiter(Thread.currentThread());
        do
        {
            Waiter current = waiterHead;
            newWaiter.next = current;
            if (unsafe.compareAndSwapObject(this, offset, current, newWaiter))
            {
                break;
            }
        } while (true);
        return newWaiter;
    }
    
    private Waiter getSignalHead()
    {
        Waiter newWaiter = new Waiter(null);
        do
        {
            Waiter head = waiterHead;
            if (unsafe.compareAndSwapObject(this, offset, head, newWaiter))
            {
                return head;
            }
        } while (true);
    }
    
    @Override
    public void signallBlockwaiting()
    {
        Waiter current = waiterHead;
        if (current.thread == null)
        {
            return;
        }
        Waiter head = getSignalHead();
        do
        {
            LockSupport.unpark(head.thread);
        } while ((head = head.next) != null);
    }
    
}
