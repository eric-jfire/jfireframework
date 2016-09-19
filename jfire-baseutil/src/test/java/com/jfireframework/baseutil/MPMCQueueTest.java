package com.jfireframework.baseutil;

import com.jfireframework.baseutil.concurrent.MPMCQueue;
import org.testng.annotations.Test;

import java.util.concurrent.CountDownLatch;

/**
 * Created by linbin on 2016/9/19.
 */
public class MPMCQueueTest
{
    @Test
    public void test() throws InterruptedException
    {
        final MPMCQueue<String> queue = new MPMCQueue<String>();
        final int count = 10000;
        final int threadSum = 1;
        final int countThreadSum = 5;
        for (int i = 0; i < threadSum; i++)
        {
            new Thread(
                    new Runnable() {
                        @Override
                        public void run()
                        {
                            for (int i = 0; i < count; i++)
                            {
                                queue.offerAndSignal("1");
                            }
                            System.out.println(Thread.currentThread().getName() + "输出完毕");
                        }
                    }, "insert-" + i
            ).start();
        }
        final CountDownLatch latch = new CountDownLatch(count * threadSum);
        for (int i = 0; i < countThreadSum; i++)
        {
            Thread countThread = new Thread(
                    new Runnable() {
                        @Override
                        public void run()
                        {
                            int sum = count * threadSum;
                            for (int i = 0; i < sum; i++)
                            {
                                queue.take();
                                latch.countDown();
                            }
                            if (queue.poll() == null)
                            {
                                System.out.println("完成");
                            }
                            else
                            {
                                System.err.println("异常");
                            }
                        }
                    }, "take-" + i
            );
            countThread.start();
        }
        latch.await();
        Thread.sleep(1000000);
        System.out.println("完成");
    }
}
