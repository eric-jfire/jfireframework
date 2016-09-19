package com.jfireframework.baseutil.disruptor;

import java.util.concurrent.CyclicBarrier;

import org.junit.Test;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.DirectByteBuf;
import com.jfireframework.baseutil.disruptor.waitstrategy.ParkWaitStrategy;
import com.jfireframework.baseutil.time.Timewatch;

public class ParkTest
{
    private int threadStart = 100;
    private int threadEnd   = 200;
    private int sendCount   = 20;
    private int actionSize  = 16;
    private int capacity    = 2;
    
    @Test
    public void test()
    {
        EntryAction[] actions = new EntryAction[actionSize];
        for (int i = 0; i < actionSize; i++)
        {
            actions[i] = new TestAction();
        }
        Thread[] threads = new Thread[actionSize];
        for (int i = 0; i < actionSize; i++)
        {
            threads[i] = new Thread(actions[i], "disruptor-" + i);
        }
        ParkWaitStrategy waitStrategy = new ParkWaitStrategy(threads);
        final Disruptor disruptor = new Disruptor(capacity, actions, threads, waitStrategy);
        for (int index = threadStart; index <= threadEnd; index++)
        {
            final CyclicBarrier barrier = new CyclicBarrier(index);
            Thread[] testThreads = new Thread[index];
            for (int i = 0; i < testThreads.length; i++)
            {
                testThreads[i] = new Thread(
                        new Runnable() {
                            
                            @Override
                            public void run()
                            {
                                try
                                {
                                    barrier.await();
                                    ByteBuf<?> buf = DirectByteBuf.allocate(1);
                                    for (int count = 0; count < sendCount; count++)
                                    {
                                        buf.readIndex(6);
                                        disruptor.publish(buf);
                                    }
                                }
                                catch (Throwable e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }, "测试线程_" + index + "_" + i
                );
                testThreads[i].start();
            }
            Timewatch timewatch = new Timewatch();
            timewatch.start();
            try
            {
                for (int i = 0; i < testThreads.length; i++)
                {
                    testThreads[i].join();
                }
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            timewatch.end();
            System.out.println("线程数量：" + index + ",运行完毕:" + timewatch.getTotal());
        }
    }
}
