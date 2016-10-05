package com.jfireframework.baseutil.time;

public class NanoTimeWatch
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
    
    public long getTatol()
    {
        return t1 - t0;
    }
}
