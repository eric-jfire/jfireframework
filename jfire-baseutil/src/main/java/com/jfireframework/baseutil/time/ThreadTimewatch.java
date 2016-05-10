package com.jfireframework.baseutil.time;

public class ThreadTimewatch
{
    private static ThreadLocal<Long> t0 = new ThreadLocal<Long>();
    private static ThreadLocal<Long> t1 = new ThreadLocal<Long>();
    
    public static void start()
    {
        t0.set(System.currentTimeMillis());
    }
    
    public static void end()
    {
        t1.set(System.currentTimeMillis());
    }
    
    public static long getTotalTime()
    {
        return t1.get() - t0.get();
    }
}
