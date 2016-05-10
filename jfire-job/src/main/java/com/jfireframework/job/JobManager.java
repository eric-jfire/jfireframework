package com.jfireframework.job;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import com.jfireframework.job.trigger.Trigger;
import com.jfireframework.job.trigger.impl.AbstractTrigger;

/**
 * 任务管理器，主要是负责任务的加入和删除。以及分配资源给予任务运行
 * 
 * @author 林斌{erci@jfire.cn}
 * 
 */
public class JobManager
{
    private BlockingQueue<Trigger> triggers    = new PriorityBlockingQueue<Trigger>(100, new TimeComparator());
    private JobExecutor            jobExecutor = new JobExecutor(Runtime.getRuntime().availableProcessors() * 2);
    private Lock                   lock        = new ReentrantLock();
    private Condition              toEndTime   = lock.newCondition();
    private ScanJob                scanJob;
    
    public JobManager()
    {
        
    }
    
    public JobManager(int size)
    {
        jobExecutor = new JobExecutor(size);
    }
    
    public void start()
    {
        scanJob = new ScanJob(triggers, lock, toEndTime, jobExecutor);
        new Thread(scanJob).start();
    }
    
    public void shutdown()
    {
        if (scanJob != null)
        {
            scanJob.shutdown();
        }
        jobExecutor.shutdown();
    }
    
    /**
     * 在任务管理器中删除该任务
     * 
     * @param jobTrigger
     */
    public void removeJob(Trigger jobTrigger)
    {
        jobTrigger.removeJob();
    }
    
    public void addTrigger(Trigger jobTrigger)
    {
        ((AbstractTrigger) jobTrigger).setJobManager(this);
        triggers.offer(jobTrigger);
        lock.lock();
        try
        {
            toEndTime.signal();
        }
        finally
        {
            lock.unlock();
        }
    }
    
    public int size()
    {
        return triggers.size();
    }
}

class ScanJob implements Runnable
{
    private BlockingQueue<Trigger> triggers;
    private Condition              toEndTime;
    private boolean                isShutdown = false;
    private JobExecutor            jobExecutor;
    private Lock                   lock;
    
    public ScanJob(BlockingQueue<Trigger> triggers, Lock lock, Condition toEndTime, JobExecutor jobExecutor)
    {
        this.triggers = triggers;
        this.toEndTime = toEndTime;
        this.jobExecutor = jobExecutor;
        this.lock = lock;
    }
    
    @Override
    public void run()
    {
        while (isShutdown == false)
        {
            Trigger maxNextjobTrigger = null;
            try
            {
                do
                {
                    maxNextjobTrigger = triggers.take();
                    if (maxNextjobTrigger != null)
                    {
                        if (maxNextjobTrigger.removed())
                        {
                            continue;
                        }
                        break;
                    }
                } while (isShutdown == false);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            long now = System.currentTimeMillis();
            if (now < maxNextjobTrigger.nextTriggerTime())
            {
                try
                {
                    lock.lock();
                    toEndTime.await(maxNextjobTrigger.nextTriggerTime() - now, TimeUnit.MILLISECONDS);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    lock.unlock();
                }
            }
            if (System.currentTimeMillis() >= maxNextjobTrigger.nextTriggerTime())
            {
                jobExecutor.addJob(maxNextjobTrigger);
            }
            else
            {
                // 如果是提前唤醒，则意味着有新的可执行任务被添加或者是某一个任务已经执行完毕可以重新参与计算时间
                // 此时应该将之前获得任务重新放入进入下一轮计算
                triggers.offer(maxNextjobTrigger);
                continue;
            }
        }
    }
    
    public void shutdown()
    {
        isShutdown = true;
    }
    
}
