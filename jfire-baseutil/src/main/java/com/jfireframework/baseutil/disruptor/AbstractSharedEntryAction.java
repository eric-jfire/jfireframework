package com.jfireframework.baseutil.disruptor;

import java.util.concurrent.atomic.AtomicLong;
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
    private AtomicLong                cursor = new AtomicLong(0);
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
            if (preActions.length > 0)
            {
                for (SharedEntryAction eachPre : preActions)
                {
                    while (eachPre.cursor() <= _cursor)
                    {
                        
                    }
                }
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
            cursor.lazySet(_cursor + 1);
        }
    }
    
    @Override
    public long cursor()
    {
        return cursor.get();
    }
    
    public abstract void doJob(Entry entry);
    
    public void setRingArray(RingArray ringArray)
    {
        this.ringArray = ringArray;
        // 这样是为了保证可见性
        canRun = 1;
    }
    
}
