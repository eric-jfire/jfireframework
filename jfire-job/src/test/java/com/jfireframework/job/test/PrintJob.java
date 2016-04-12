package com.jfireframework.job.test;

import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.job.Job;

public class PrintJob implements Job
{
    private Logger logger = ConsoleLogFactory.getLogger();
    private int    count  = 1;
    
    @Override
    public void doJob()
    {
        logger.info("这是第" + count + "次打印");
        count++;
    }
    
    @Override
    public boolean nextRound()
    {
        return true;
    }
}
