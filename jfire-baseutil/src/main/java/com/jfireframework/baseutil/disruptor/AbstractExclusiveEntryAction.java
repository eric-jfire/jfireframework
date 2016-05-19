package com.jfireframework.baseutil.disruptor;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;
import com.jfireframework.baseutil.disruptor.ringarray.RingArray;
import com.jfireframework.baseutil.disruptor.waitstrategy.WaitStrategyStopException;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;

/**
 * 
 * 抽象的独占entry处理器。独占处理器只允许一个entry被一个处理器取得并消费
 * 
 * @author 林斌
 * 
 */
public abstract class AbstractExclusiveEntryAction implements ExclusiveEntryAction
{
    // 当前准备处理的序号
    private AtomicLong   cursor = new AtomicLong(0);
    protected Logger     logger = ConsoleLogFactory.getLogger();
    protected RingArray  ringArray;
    private volatile int canRun = 0;
    
    @Override
    public void run()
    {
        while (canRun == 0)
        {
            LockSupport.parkNanos(1000);
            continue;
        }
        Entry entry;
        while (true)
        {
            long _cursor = cursor.get();
            if (ringArray.isAvailable(_cursor) == false)
            {
                try
                {
                    logger.debug("等待序号:{}", cursor);
                    ringArray.waitFor(_cursor);
                }
                catch (WaitStrategyStopException e)
                {
                    logger.error("停止");
                    break;
                }
            }
            entry = ringArray.entryAt(_cursor);
            if (entry.take() == false)
            {
                cursor.set(_cursor + 1);
                continue;
            }
            try
            {
                doJob(entry);
            }
            catch (Exception e)
            {
                logger.error("出现异常", e);
                ringArray.stop();
                break;
            }
            cursor.set(_cursor + 1);
        }
    }
    
    public abstract void doJob(Entry entry);
    
    public void setRingArray(RingArray ringArray)
    {
        this.ringArray = ringArray;
        canRun = 1;
    }
    
    public long cursor()
    {
        return cursor.get();
    }
    
}
