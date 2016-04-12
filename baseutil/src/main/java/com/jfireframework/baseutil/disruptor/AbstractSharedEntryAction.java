package com.jfireframework.baseutil.disruptor;

import com.jfireframework.baseutil.disruptor.ringarray.RingArray;
import com.jfireframework.baseutil.disruptor.ringarray.RingArrayStopException;
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
    private volatile long             cursor       = 0;
    protected Logger                  logger       = ConsoleLogFactory.getLogger();
    protected RingArray               ringArray;
    protected Disruptor               disruptor;
    private final SharedEntryAction[] preActions;
    private volatile int              getRingArray = 0;
                                                   
    public AbstractSharedEntryAction(SharedEntryAction... preActions)
    {
        this.preActions = preActions;
    }
    
    @Override
    public void run()
    {
        while (getRingArray == 0)
        {
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
                catch (RingArrayStopException e)
                {
                    logger.error("停止");
                    break;
                }
            }
            entry = ringArray.entryAt(cursor);
            for (SharedEntryAction eachPre : preActions)
            {
                while (eachPre.cursor() <= cursor)
                {
                
                }
            }
            try
            {
                doJob(entry);
            }
            catch (Exception e)
            {
                logger.error("出现异常", e);
                disruptor.stop();
                break;
            }
            cursor += 1;
        }
    }
    
    @Override
    public long cursor()
    {
        return cursor;
    }
    
    public abstract void doJob(Entry entry);
    
    public void setRingArray(RingArray ringArray)
    {
        this.ringArray = ringArray;
        // 这样是为了保证可见性
        getRingArray = 1;
    }
    
    public void publish(Object data)
    {
        ringArray.publish(data);
    }
    
    @Override
    public void setDisruptor(Disruptor disruptor)
    {
        this.disruptor = disruptor;
        ringArray = disruptor.getRingArray();
        // 这样是为了保证可见性
        getRingArray = 1;
    }
}
