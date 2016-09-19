package com.jfireframework.baseutil.autonomydisruptor;

import com.jfireframework.baseutil.concurrent.CpuCachePadingInt;
import com.jfireframework.baseutil.concurrent.CpuCachePadingLong;
import com.jfireframework.baseutil.disruptor.Entry;
import com.jfireframework.baseutil.disruptor.ringarray.RingArray;
import com.jfireframework.baseutil.disruptor.waitstrategy.WaitStrategyStopException;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;

public abstract class AutonomyExclusiveEntryAction implements AutonomyEntryAction
{
    // 当前准备处理的序号
    private CpuCachePadingLong        cursor  = new CpuCachePadingLong(0);
    protected static final Logger     logger  = ConsoleLogFactory.getLogger();
    protected final AutonomyRingArray ringArray;
    protected final int               MAX_RETRY_SUM;
    protected final CpuCachePadingInt idleCount;
    protected static final int        running = 1;
    protected static final int        exit    = 0;
    protected volatile int            flag    = running;
    
    public AutonomyExclusiveEntryAction(AutonomyRingArray ringArray, long cursor, int maxRetrySum)
    {
        idleCount = ringArray.idleCount();
        this.ringArray = ringArray;
        this.cursor.set(cursor);
        MAX_RETRY_SUM = maxRetrySum;
    }
    
    @Override
    public void run()
    {
        int retryCount = 0;
        try
        {
            while (true)
            {
                if (flag == exit)
                {
                    System.out.println("退出");
                    return;
                }
                long t_cursor = cursor.value();
                if (ringArray.isAvailable(t_cursor) == false)
                {
                    try
                    {
                        logger.debug("等待序号:{}", cursor);
                        ringArray.waitFor(t_cursor);
                    }
                    catch (WaitStrategyStopException e)
                    {
                        logger.error("停止");
                        break;
                    }
                }
                Entry entry = ringArray.entryAt(t_cursor);
                int result = entry.takeReturnMore();
                if (result == Entry.ignore)
                {
                    cursor.set(t_cursor + 1);
                    continue;
                }
                else if (result == Entry.takeFail)
                {
                    retryCount += 1;
                    if (retryCount == MAX_RETRY_SUM)
                    {
                        if (ringArray.removeAction(this))
                        {
                            cursor.set(t_cursor + 1);
                            continue;
                        }
                        else
                        {
                            retryCount = 0;
                        }
                    }
                    cursor.set(t_cursor + 1);
                    continue;
                }
                retryCount = 0;
                idleCount.decreaseAndGet();
                Object data = entry.getData();
                cursor.set(t_cursor + 1);
                doJob(data);
                idleCount.increaseAndGet();
            }
        }
        catch (Exception e)
        {
            logger.error("出现异常", e);
            ringArray.stop();
            idleCount.increaseAndGet();
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
        return cursor.value();
    }
    
    @Override
    public void publish(Object data)
    {
        ringArray.publish(data);
    }
    
    public void stop()
    {
        flag = exit;
    }
}
