package com.jfireframework.baseutil.disruptor.waitstrategy;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import com.jfireframework.baseutil.disruptor.ringarray.RingArray;
import com.jfireframework.baseutil.disruptor.ringarray.RingArrayStopException;

public final class BlockWaitStrategy implements WaitStrategy
{
    private Lock      lock               = new ReentrantLock();
    private Condition messageCanBeHandle = lock.newCondition();
                                         
    @Override
    public void waitFor(long next, RingArray array) throws RingArrayStopException
    {
        lock.lock();
        try
        {
            while (array.isAvailable(next) == false)
            {
                messageCanBeHandle.await();
                if (array.stoped())
                {
                    throw RingArrayStopException.instance;
                }
            }
        }
        catch (InterruptedException e)
        {
            throw RingArrayStopException.instance;
        }
        finally
        {
            lock.unlock();
        }
    }
    
    @Override
    public void signallBlockwaiting()
    {
        lock.lock();
        try
        {
            messageCanBeHandle.signalAll();
        }
        finally
        {
            lock.unlock();
        }
    }
    
}
