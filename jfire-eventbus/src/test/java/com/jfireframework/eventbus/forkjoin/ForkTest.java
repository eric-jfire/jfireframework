package com.jfireframework.eventbus.forkjoin;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import org.junit.Test;
import com.jfireframework.eventbus.Print;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.bus.impl.FlexibleQueueEventBusImpl;
import com.jfireframework.eventbus.util.AtomicIntergerIdleCount;

public class ForkTest
{
    @Test
    public void test()
    {
        // EventBus bus = new ManualEventBusImpl();
        EventBus bus = new FlexibleQueueEventBusImpl(new AtomicIntergerIdleCount(), 200, 3);
        bus.addHandler(new PrintForkHandler(true));
        bus.start();
        Queue<String> queue = new LinkedBlockingQueue<String>();
        for (int i = 0; i < 16; i++)
        {
            queue.offer(String.valueOf(i));
        }
        bus.post(queue, Print.one).await();
    }
}
