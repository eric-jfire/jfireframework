package com.jfireframework.baseutil;

import java.util.concurrent.TimeUnit;
import org.junit.Ignore;
import org.junit.Test;
import com.jfireframework.baseutil.concurrent.time.DefaultTimeoutHandler;
import com.jfireframework.baseutil.concurrent.time.FixedCapacityWheelTimer;
import com.jfireframework.baseutil.concurrent.time.HierarchyWheelTimer;
import com.jfireframework.baseutil.concurrent.time.TimeTask;
import com.jfireframework.baseutil.concurrent.time.Timer;

public class TimerTest
{
    @Test
    @Ignore
    public void test() throws InterruptedException
    {
        Timer timer = new FixedCapacityWheelTimer(20, 500);
        final long t0 = System.currentTimeMillis();
        timer.addTask(
                new TimeTask() {
                    
                    @Override
                    public void invoke()
                    {
                        System.out.println(System.currentTimeMillis() - t0);
                    }
                }, 1, TimeUnit.SECONDS
        );
        Thread.sleep(2000);
    }
    
    @Test
    public void test2() throws InterruptedException
    {
        Timer timer = new HierarchyWheelTimer(new int[] { 4, 2, 2 }, 100, new DefaultTimeoutHandler());
        final long t0 = System.currentTimeMillis();
        timer.addTask(
                new TimeTask() {
                    
                    @Override
                    public void invoke()
                    {
                        System.out.println(System.currentTimeMillis() - t0);
                    }
                }, 500, TimeUnit.MILLISECONDS
        );
        Thread.sleep(2000);
    }
}
