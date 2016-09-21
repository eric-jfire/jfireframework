package com.jfireframework.eventbus;

import org.junit.Test;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.bus.impl.FlexibleQueueEventBus;
import com.jfireframework.eventbus.eventthread.AtomicIntergerIdleCount;

public class PrintTest
{
    @Test
    public void test() throws InterruptedException
    {
        EventBus bus = new FlexibleQueueEventBus(new AtomicIntergerIdleCount(), 1000, 1);
        bus.addHandler(new PrintHandler());
        bus.start();
        bus.post("1", Print.one);
        bus.post("2", Print.one);
        bus.post("3", Print.one);
        bus.post("4", Print.one);
        bus.post("5", Print.one);
        Thread.sleep(100000);
    }
}
