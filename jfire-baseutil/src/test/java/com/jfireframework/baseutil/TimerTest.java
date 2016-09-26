package com.jfireframework.baseutil;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.jfireframework.baseutil.concurrent.time.*;
import com.jfireframework.baseutil.timer.DefaultTimeoutHandler;
import com.jfireframework.baseutil.timer.FixedCapacityWheelTimer;
import com.jfireframework.baseutil.timer.HierarchyWheelTimer;
import com.jfireframework.baseutil.timer.TimeTask;
import com.jfireframework.baseutil.timer.Timer;

public class TimerTest
{
    @Test
    public void test() throws InterruptedException
    {
        Timer timer = new FixedCapacityWheelTimer(2048, 1, TimeUnit.MILLISECONDS);
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
        Timer timer = new HierarchyWheelTimer(new int[] { 700, 2, 2 }, 1, TimeUnit.MILLISECONDS, new DefaultTimeoutHandler());
        final long t0 = System.currentTimeMillis();
        timer.addTask(
                new TimeTask() {
                    
                    @Override
                    public void invoke()
                    {
                        System.out.println("耗时：" + (System.currentTimeMillis() - t0));
                    }
                }, 800, TimeUnit.MILLISECONDS
        );
        Thread.sleep(2000);
    }
}
