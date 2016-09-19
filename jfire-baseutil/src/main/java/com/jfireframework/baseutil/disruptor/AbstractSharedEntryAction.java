package com.jfireframework.baseutil.disruptor;

import java.util.concurrent.locks.LockSupport;

import com.jfireframework.baseutil.disruptor.ringarray.RingArray;
import com.jfireframework.baseutil.disruptor.waitstrategy.WaitStrategyStopException;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;

/**
 * 抽象的共享entry处理器。共享处理器，允许同一个entry被所有的处理器消费
 * 并且处理器之间可以形成前后的依赖关系
 * 
 * @author 林斌
 * 
 */
public abstract class AbstractSharedEntryAction implements SharedEntryAction
{
    // 当前准备处理的序号
    private long                      cursor = 0;
    protected Logger                  logger = ConsoleLogFactory.getLogger();
    protected RingArray               ringArray;
    private final SharedEntryAction[] preActions;
    private volatile int              canRun = 0;
    
    public AbstractSharedEntryAction(SharedEntryAction... preActions)
    {
        this.preActions = preActions;
    }
    
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
            if (preActions.length > 0)
            {
                for (SharedEntryAction eachPre : preActions)
                {
                    while (eachPre.cursor() <= cursor)
                    {
                        ;// 这是一个不太好的做法，忙等待。但是不是好的做法
                    }
                }
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
    
    @Override
    public long cursor()
    {
        return cursor;
    }
    
    public abstract <T> void doJob(T data);
    
    public void setRingArray(RingArray ringArray)
    {
        this.ringArray = ringArray;
        // 这样是为了保证可见性
        canRun = 1;
    }
    
}
