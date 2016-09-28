package com.jfireframework.baseutil;

import java.util.concurrent.TimeUnit;
import org.junit.Test;
import com.jfireframework.baseutil.concurrent.MPMCQueue;

public class Mpmcbugtest
{
    @Test
    public void test() throws InterruptedException
    {
        final MPMCQueue<String> queue = new MPMCQueue<String>();
        Thread give = new Thread(
                new Runnable() {
                    @Override
                    public void run()
                    {
                        for (int i = 0; i < 10; i++)
                        {
                            for (int j = 0; j < 10; j++)
                            {
                                queue.offerAndSignal("1");
                            }
                        }
                    }
                }
        );
        Thread[] takes = new Thread[5];
        for (int i = 0; i < 4; i++)
        {
            takes[i] = new Thread(
                    new Runnable() {
                        @Override
                        public void run()
                        {
                            while (true)
                            {
                                queue.take();
                            }
                        }
                    }, "take-" + i
            );
        }
        takes[4] = new Thread(
                new Runnable() {
                    @Override
                    public void run()
                    {
                        while (true)
                        {
                            queue.take(1, TimeUnit.SECONDS);
                        }
                    }
                }, "time"
        );
        for (Thread each : takes)
        {
            each.start();
        }
        give.start();
        give.join();
        System.out.println("发放完毕");
        Thread.sleep(1000000);
    }
}
