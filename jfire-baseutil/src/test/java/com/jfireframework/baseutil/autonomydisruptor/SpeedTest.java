package com.jfireframework.baseutil.autonomydisruptor;

import java.util.concurrent.CountDownLatch;
import org.junit.Test;
import com.jfireframework.baseutil.time.Timewatch;

public class SpeedTest
{
    @Test
    public void test() throws InterruptedException
    {
        final int count = 1000000;
        final TicketCount ticketCount = new TicketCount(count);
        final AutonomyDisruptor disruptor = new AutonomyDisruptor(1024, new EntryActionFactory() {
            
            @Override
            public AutonomyEntryAction newEntryAction(AutonomyRingArray ringArray, long cursor)
            {
                return new AutonomyExclusiveEntryAction(ringArray, cursor, 50) {
                    
                    @Override
                    public <T> void doJob(T data)
                    {
                        // ticketCount.countDown();
                    }
                };
            }
        });
        Timewatch timewatch = new Timewatch();
        Thread[] threads = new Thread[10];
        for (int index = 0; index < threads.length; index++)
        {
            threads[index] = new Thread(new Runnable() {
                
                @Override
                public void run()
                {
                    for (int i = 0; i < count; i++)
                    {
                        disruptor.publish("");
                    }
                }
            });
            threads[index].start();
        }
        for (Thread each : threads)
        {
            each.join();
        }
        // ticketCount.await();
        timewatch.end();
        System.out.println(timewatch.getTotal());
        System.out.println(disruptor.idleCount().value());
    }
}
