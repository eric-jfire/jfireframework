package com.jfireframework.baseutil.disruptor.waitstrategy;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import com.jfireframework.baseutil.disruptor.ringarray.RingArray;

public final class BlockWaitStrategy extends AbstractWaitStrategy
{
    private Lock      lock               = new ReentrantLock();
    private Condition messageCanBeHandle = lock.newCondition();
    
    @Override
    public void waitFor(long next, RingArray array) throws WaitStrategyStopException
    {
        lock.lock();
        try
        {
            while (array.isAvailable(next) == false)
            {
                messageCanBeHandle.await();
                detectStopException();
            }
        }
        catch (InterruptedException e)
        {
            throw WaitStrategyStopException.instance;
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
