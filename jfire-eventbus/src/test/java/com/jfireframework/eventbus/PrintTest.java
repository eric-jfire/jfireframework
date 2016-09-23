package com.jfireframework.eventbus;

import java.util.LinkedList;
import java.util.List;
import org.junit.Test;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.bus.impl.FlexibleQueueEventBusImpl;
import com.jfireframework.eventbus.event.EventContext;
import com.jfireframework.eventbus.eventthread.AtomicIntergerIdleCount;

public class PrintTest
{
    @Test
    public void test() throws InterruptedException
    {
        EventBus bus = new FlexibleQueueEventBusImpl(new AtomicIntergerIdleCount(), 200, 1);
        bus.addHandler(new PrintHandler());
        bus.addHandler(new RowPrint());
        bus.start();
        // bus.post("1", Print.one);
        // bus.post("2", Print.one);
        // bus.post("3", Print.one);
        // bus.post("4", Print.one);
        // bus.post("5", Print.one).await();
        List<EventContext> eventContexts = new LinkedList<EventContext>();
        eventContexts.add(bus.post("1", Print.single, "1"));
        eventContexts.add(bus.post("2", Print.single, "1"));
        eventContexts.add(bus.post("3", Print.single, "1"));
        eventContexts.add(bus.post("4", Print.single, "1"));
        eventContexts.add(bus.post("5", Print.single, "1"));
        eventContexts.add(bus.post("6", Print.single, "1"));
        for (EventContext each : eventContexts)
        {
            each.await();
        }
    }
}
