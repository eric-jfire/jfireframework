package com.jfireframework.baseutil.disruptor;

import com.jfireframework.baseutil.disruptor.ringarray.RingArray;
import com.jfireframework.baseutil.disruptor.ringarray.RingArrayStopException;
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
    private volatile long cursor       = 0;
    protected Logger      logger       = ConsoleLogFactory.getLogger();
    protected RingArray   ringArray;
    protected Disruptor   disruptor;
    private volatile int  getRingArray = 0;
                                       
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
            if (entry.take() == false)
            {
                cursor += 1;
                continue;
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
    
    public abstract void doJob(Entry entry);
    
    public long cursor()
    {
        return cursor;
    }
    
    @Override
    public void setDisruptor(Disruptor disruptor)
    {
        this.disruptor = disruptor;
        ringArray = disruptor.getRingArray();
        // 这样是为了保证可见性
        getRingArray = 1;
    }
    
    public void publish(Object data)
    {
        ringArray.publish(data);
    }
}
