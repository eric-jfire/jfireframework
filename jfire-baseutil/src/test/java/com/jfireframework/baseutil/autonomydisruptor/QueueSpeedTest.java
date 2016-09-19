package com.jfireframework.baseutil.autonomydisruptor;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.junit.Test;

import com.jfireframework.baseutil.concurrent.MPMCCpuCacheQueue;
import com.jfireframework.baseutil.concurrent.MPMCQueue;
import com.jfireframework.baseutil.time.Timewatch;

public class QueueSpeedTest
{
    private int sendThread = 10;
    private int getThread  = 10;
    
    @Test
    public void test3() throws InterruptedException
    {
        final int count = 1000000;
        final ConcurrentLinkedQueue<Object> queue = new ConcurrentLinkedQueue<Object>();
        for (int i = 0; i < getThread; i++)
        {
            new Thread(
                    new Runnable() {
                        
                        @Override
                        public void run()
                        {
                            int sum = 0;
                            Timewatch timewatch = new Timewatch();
                            while (true)
                            {
                                // if (queue.poll() != null)
                                // {
                                // sum += 1;
                                // if (sum == count)
                                // {
                                // break;
                                // }
                                // }
                                queue.poll();
                                // sum += 1;
                                // if (sum == count)
                                // {
                                // break;
                                // }
                            }
                        }
                    }
            ).start();
        }
        Timewatch timewatch = new Timewatch();
        Thread[] threads = new Thread[sendThread];
        for (int index = 0; index < threads.length; index++)
        {
            threads[index] = new Thread(
                    new Runnable() {
                        
                        @Override
                        public void run()
                        {
                            for (int i = 0; i < count; i++)
                            {
                                queue.offer("");
                            }
                        }
                    }
            );
            threads[index].start();
            
        }
        for (Thread each : threads)
        {
            each.join();
        }
        timewatch.end();
        System.out.println("发送耗时：" + timewatch.getTotal());
    }
    
    @Test
    public void test1() throws InterruptedException
    {
        final int count = 1000000;
        final MPMCCpuCacheQueue<Object> queue = new MPMCCpuCacheQueue<Object>();
        for (int i = 0; i < getThread; i++)
        {
            new Thread(
                    new Runnable() {
                        
                        @Override
                        public void run()
                        {
                            int sum = 0;
                            Timewatch timewatch = new Timewatch();
                            while (true)
                            {
                                // if (queue.poll() != null)
                                // {
                                // sum += 1;
                                // if (sum == count)
                                // {
                                // break;
                                // }
                                // }
                                queue.poll();
                                // sum += 1;
                                // if (sum == count)
                                // {
                                // break;
                                // }
                            }
                        }
                    }
            ).start();
        }
        Timewatch timewatch = new Timewatch();
        Thread[] threads = new Thread[sendThread];
        for (int index = 0; index < threads.length; index++)
        {
            threads[index] = new Thread(
                    new Runnable() {
                        
                        @Override
                        public void run()
                        {
                            for (int i = 0; i < count; i++)
                            {
                                queue.offer("");
                            }
                        }
                    }
            );
            threads[index].start();
            
        }
        for (Thread each : threads)
        {
            each.join();
        }
        timewatch.end();
        System.out.println("发送耗时：" + timewatch.getTotal());
    }
    
    @Test
    public void test2() throws InterruptedException
    {
        final int count = 1000000;
        final MPMCQueue<Object> queue = new MPMCQueue<Object>();
        for (int i = 0; i < getThread; i++)
        {
            new Thread(
                    new Runnable() {
                        
                        @Override
                        public void run()
                        {
                            int sum = 0;
                            Timewatch timewatch = new Timewatch();
                            while (true)
                            {
                                // if (queue.poll() != null)
                                // {
                                // sum += 1;
                                // if (sum == count)
                                // {
                                // break;
                                // }
                                // }
                                queue.poll();
                                // sum += 1;
                                // if (sum == count)
                                // {
                                // break;
                                // }
                            }
                        }
                    }
            ).start();
        }
        Timewatch timewatch = new Timewatch();
        Thread[] threads = new Thread[sendThread];
        for (int index = 0; index < threads.length; index++)
        {
            threads[index] = new Thread(
                    new Runnable() {
                        
                        @Override
                        public void run()
                        {
                            for (int i = 0; i < count; i++)
                            {
                                queue.offer("");
                            }
                        }
                    }
            );
            threads[index].start();
            
        }
        for (Thread each : threads)
        {
            each.join();
        }
        timewatch.end();
        System.out.println("发送耗时：" + timewatch.getTotal());
    }
}
