package com.jfireframework.job;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class JobExecutor
{
    private ExecutorCompletionService<Void> executorService;
    private int                             size;
    private int                             count    = 0;
    private ExecutorService                 pool;
    private boolean                         shutdown = false;
    
    public JobExecutor(int size)
    {
        pool = Executors.newFixedThreadPool(size, new ThreadFactory() {
            private int count = 1;
            
            @Override
            public Thread newThread(Runnable r)
            {
                return new Thread(r, "jfire-job-thread-" + (count++));
            }
        });
        executorService = new ExecutorCompletionService<>(pool);
        this.size = size;
    }
    
    public void addJob(Callable<Void> task)
    {
        if (shutdown)
        {
            return;
        }
        executorService.submit(task);
        count++;
        while (executorService.poll() != null)
        {
            count--;
        }
        while (count >= size)
        {
            try
            {
                executorService.take();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            count--;
        }
    }
    
    public void shutdown()
    {
        shutdown = true;
        pool.shutdownNow();
    }
}
