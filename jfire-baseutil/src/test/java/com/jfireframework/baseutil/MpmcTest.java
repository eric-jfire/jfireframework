package com.jfireframework.baseutil;

import java.util.Queue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import org.junit.Test;
import com.jfireframework.baseutil.concurrent.MPMCQueue;
import com.jfireframework.baseutil.time.NanoTimeWatch;
import com.jfireframework.baseutil.time.Timewatch;

public class MpmcTest
{
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
        final Queue<Integer> queue = new ConcurrentLinkedQueue<Integer>();
        final CountDownLatch latch = new CountDownLatch(1);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run()
            {
                try
                {
                    Integer value;
                    for (int i = 0; i < count; i++)
                    {
                        value = queue.poll();
                        if (value != null)
                        {
                            ;
                        }
                        else
                        {
                            while ((value = queue.poll()) == null)
                            {
                                ;
                            }
                        }
                        if (i != value.intValue())
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
        });
        NanoTimeWatch watch = new NanoTimeWatch();
        watch.start();
        thread.start();
        for (int i = 0; i < count; i++)
        {
            queue.add(source[i]);
        }
        latch.await();
        watch.end();
        System.out.println(watch.getTatol() / 1000 / 1000);
    }
}
