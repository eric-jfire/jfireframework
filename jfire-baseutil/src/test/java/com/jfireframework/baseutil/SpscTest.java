package com.jfireframework.baseutil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import org.junit.Test;
import com.jfireframework.baseutil.concurrent.SpscQueue;
import com.jfireframework.baseutil.time.NanoTimeWatch;
import com.jfireframework.baseutil.time.Timewatch;

public class SpscTest
{
    @Test
    public void test2()
    {
        final int count = 100000000;
        int[] array = new int[count];
        Timewatch timewatch = new Timewatch();
        timewatch.start();
        for (int i = 0; i < count; i++)
        {
            array[i] = 1;
        }
        timewatch.end();
        System.out.println(timewatch.getTotal());
    }
    
    @Test
    public void test() throws InterruptedException, BrokenBarrierException
    {
        final int count = 10000000;
        Integer[] source = new Integer[count];
        Timewatch timewatch = new Timewatch();
        timewatch.start();
        for (int i = 0; i < count; i++)
        {
            source[i] = Integer.valueOf(i);
        }
        timewatch.end();
        System.out.println("数据生产耗时:" + timewatch.getTotal());
        final SpscQueue<Integer> queue = new SpscQueue<Integer>(512);
        final CountDownLatch latch = new CountDownLatch(1);
        Thread thread = new Thread(
                new Runnable() {
                    @Override
                    public void run()
                    {
                        try
                        {
                            for (int i = 0; i < count; i++)
                            {
                                if (i != queue.get().intValue())
                                {
                                    System.err.println("数据异常");
                                }
                            }
                            latch.countDown();
                        }
                        catch (Exception e)
                        {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        
                    }
                }
        );
        NanoTimeWatch watch = new NanoTimeWatch();
        watch.start();
        thread.start();
        for (int i = 0; i < count; i++)
        {
            queue.put(source[i]);
        }
        latch.await();
        watch.end();
        System.out.println(watch.getTatol() / 1000 / 1000);
    }
}
