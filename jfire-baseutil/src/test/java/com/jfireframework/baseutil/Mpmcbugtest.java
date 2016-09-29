package com.jfireframework.baseutil;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import org.junit.Test;
import com.jfireframework.baseutil.concurrent.MPMCQueue;

public class Mpmcbugtest
{
    @Test
    public void test() throws InterruptedException
    {
        final MPMCQueue<String> queue = new MPMCQueue<String>(true);
        final int time = 5;
        final int count = 10;
        final CountDownLatch latch = new CountDownLatch(count * time * 2);
        new Thread(
                new Runnable() {
                    @Override
                    public void run()
                    {
                        for (int i = 0; i < time; i++)
                        {
                            for (int j = 0; j < count; j++)
                            {
                                queue.offerAndSignal("1");
                            }
                            try
                            {
                                Thread.sleep(1000);
                            }
                            catch (InterruptedException e)
                            {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                }, "give1"
        ).start();
        new Thread(
                new Runnable() {
                    @Override
                    public void run()
                    {
                        for (int i = 0; i < time; i++)
                        {
                            for (int j = 0; j < count; j++)
                            {
                                queue.offerAndSignal("1");
                            }
                            try
                            {
                                Thread.sleep(1000);
                            }
                            catch (InterruptedException e)
                            {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                }, "give2"
        ).start();
        Thread[] takes = new Thread[5];
        for (int i = 0; i < 5; i++)
        {
            if (i < 10)
            {
                takes[i] = new Thread(
                        new Runnable() {
                            @Override
                            public void run()
                            {
                                while (true)
                                {
                                    if (queue.take(10000, TimeUnit.MILLISECONDS) != null)
                                    {
                                        latch.countDown();
                                    }
                                    else
                                    {
                                    }
                                }
                            }
                        }, "take-" + i
                );
            }
            else
            {
                
                takes[i] = new Thread(
                        new Runnable() {
                            @Override
                            public void run()
                            {
                                while (true)
                                {
                                    if (queue.take() != null)
                                    {
                                        latch.countDown();
                                    }
                                    else
                                    {
                                        System.err.println("异常");
                                    }
                                }
                            }
                        }, "take-" + i
                );
            }
        }
        for (Thread each : takes)
        {
            each.start();
        }
        latch.await();
        System.out.println("完成");
        LockSupport.park();
    }
}
