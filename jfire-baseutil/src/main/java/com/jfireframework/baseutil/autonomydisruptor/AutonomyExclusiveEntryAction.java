package com.jfireframework.baseutil.autonomydisruptor;

import com.jfireframework.baseutil.disruptor.Entry;
import com.jfireframework.baseutil.disruptor.ringarray.RingArray;
import com.jfireframework.baseutil.disruptor.waitstrategy.WaitStrategyStopException;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;

public abstract class AutonomyExclusiveEntryAction implements AutonomyEntryAction
{
    // 当前准备处理的序号
    private volatile long             cursor = 0;
    protected static final Logger     logger = ConsoleLogFactory.getLogger();
    protected final AutonomyRingArray ringArray;
    protected final int               MAX_RETRY_SUM;
    
    public AutonomyExclusiveEntryAction(AutonomyRingArray ringArray, long cursor, int maxRetrySum)
    {
        this.ringArray = ringArray;
        this.cursor = cursor;
        MAX_RETRY_SUM = maxRetrySum;
    }
    
    @Override
    public void run()
    {
        int retryCount = 0;
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
            Entry entry = ringArray.entryAt(cursor);
            if (entry.take() == false)
            {
                cursor += 1;
                retryCount += 1;
                if (retryCount == MAX_RETRY_SUM)
                {
                    ringArray.removeAction(this);
                    break;
                }
                continue;
            }
            try
            {
                Object data = entry.getData();
                cursor += 1;
                doJob(data);
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
    
    @Override
    public void setRingArray(RingArray ringArray)
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public long cursor()
    {
        return cursor;
    }
    
    @Override
    public void publish(Object data)
    {
        ringArray.publish(data);
    }
    
}
