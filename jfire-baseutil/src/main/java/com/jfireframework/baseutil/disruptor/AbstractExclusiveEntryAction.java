package com.jfireframework.baseutil.disruptor;

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
    private volatile long cursor = 0;
    protected Logger      logger = ConsoleLogFactory.getLogger();
    protected RingArray   ringArray;
    private volatile int  canRun = 0;
    
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
            if (ringArray.isAvailable(cursor) == false)
            {
                try
                {
                    logger.debug("等待序号:{}", cursor);
                    ringArray.waitFor(cursor);
                }
                catch (WaitStrategyStopException e)
                {
                    logger.error("停止");
                    break;
                }
            }
            entry = ringArray.entryAt(cursor);
            if (entry.take() == false)
            {
                cursor += 1;
                continue;
            }
            try
            {
                Object data = entry.getData();
                doJob(data);
                cursor += 1;
            }
            catch (Exception e)
            {
                logger.error("出现异常", e);
                ringArray.stop();
                break;
            }
        }
    }
    
    public abstract <T> void doJob(T data);
    
    public void setRingArray(RingArray ringArray)
    {
        this.ringArray = ringArray;
        canRun = 1;
    }
    
    public long cursor()
    {
        return cursor;
    }
    
}
