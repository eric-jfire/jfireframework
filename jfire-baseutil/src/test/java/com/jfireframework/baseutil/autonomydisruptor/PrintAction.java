package com.jfireframework.baseutil.autonomydisruptor;

import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;

public class PrintAction extends AutonomyExclusiveEntryAction
{
    Logger logger = ConsoleLogFactory.getLogger(ConsoleLogFactory.DEBUG);
    
    public PrintAction(AutonomyRingArray ringArray, long cursor, int maxRetrySum)
    {
        super(ringArray, cursor, maxRetrySum);
    }
    
    @Override
    public <T> void doJob(T data)
    {
        String mString = (String) data;
        logger.debug("{} 打印 {}", Thread.currentThread().getName(), mString);
        // logger.debug("{} 睡眠", Thread.currentThread().getName());
        try
        {
            Thread.sleep(100000000);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        logger.debug("{} 唤醒", Thread.currentThread().getName());
        
    }
    
}
