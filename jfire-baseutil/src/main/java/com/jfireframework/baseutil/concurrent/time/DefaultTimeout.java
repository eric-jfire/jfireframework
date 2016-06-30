package com.jfireframework.baseutil.concurrent.time;

public class DefaultTimeout implements Timeout
{
    private volatile boolean cancel = false;
    private final Timer      timer;
    private final long       deadline;
    private final TimeTask   task;
    
    public DefaultTimeout(Timer timer, TimeTask task, long deadline)
    {
        this.timer = timer;
        this.task = task;
        this.deadline = deadline;
    }
    
    @Override
    public Timer timer()
    {
        return timer;
    }
    
    @Override
    public boolean isCanceled()
    {
        return cancel;
    }
    
    @Override
    public void cancel()
    {
        cancel = true;
    }
    
    @Override
    public void invoke()
    {
        task.invoke();
    }
    
    @Override
    public long deadline()
    {
        return deadline;
    }
    
}
