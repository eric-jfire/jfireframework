package com.jfireframework.job.test;

import org.junit.Test;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.job.JobManager;
import com.jfireframework.job.trigger.Trigger;
import com.jfireframework.job.trigger.impl.SimpleIntervalTrigger;
import com.jfireframework.job.trigger.impl.StepIntervalTrigger;

public class Function
{
    Logger logger = ConsoleLogFactory.getLogger();
    
    @Test
    public void test() throws InterruptedException
    {
        JobManager jobManager = new JobManager();
        jobManager.start();
        Trigger trigger = new SimpleIntervalTrigger(new PrintJob(), 10);
        jobManager.addTrigger(trigger);
        logger.info("开始");
        Thread.sleep(5000);
        jobManager.shutdown();
        Thread.sleep(1000000);
    }
    
    @Test
    public void testMem() throws InterruptedException
    {
        JobManager jobManager = new JobManager();
        jobManager.start();
        Trigger trigger = new SimpleIntervalTrigger(new PrintJob(), 10);
        jobManager.addTrigger(trigger);
        logger.info("开始");
        Thread.sleep(2000);
        jobManager.shutdown();
    }
    
    @Test
    public void testStep() throws InterruptedException
    {
        JobManager jobManager = new JobManager();
        Trigger trigger = new StepIntervalTrigger(new PrintJob(), 5000, 5);
        jobManager.addTrigger(trigger);
        jobManager.start();
        Thread.sleep(100000);
    }
    
    @Test
    public void testMem2() throws InterruptedException
    {
        JobManager jobManager = new JobManager();
        jobManager.start();
        for (int i = 0; i < 1000; i++)
        {
            Trigger trigger = new SimpleIntervalTrigger(new NoNextRoundJob(), 100);
            jobManager.addTrigger(trigger);
        }
        Thread.sleep(1000);
        System.out.println(jobManager.size());
        Thread.sleep(10000000);
        
    }
}
