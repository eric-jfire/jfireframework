package com.jfireframework.baseutil;

public class Timelog
{
    private long start;
    private long end;
                 
    public void start()
    {
        start = System.currentTimeMillis();
    }
    
    public void end()
    {
        end = System.currentTimeMillis();
    }
    
    public long total()
    {
        return end - start;
    }
}
