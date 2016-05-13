package com.jfireframework.baseutil.time;

public class NanoTimewatch
{
    private long t0;
    private long t1;
    
    public void start()
    {
        t0 = System.nanoTime();
    }
    
    public void end()
    {
        t1 = System.nanoTime();
    }
    
    public long getTotal()
    {
        return t1 - t0;
    }
}
