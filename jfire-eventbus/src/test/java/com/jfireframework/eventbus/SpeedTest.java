package com.jfireframework.eventbus;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import com.jfireframework.baseutil.time.Timewatch;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.bus.impl.CalculateEventBus;
import com.jfireframework.eventbus.bus.impl.IoEventBus;
import com.jfireframework.eventbus.eventcontext.EventContext;

public class SpeedTest
{
    @Test
    public void test()
    {
        int count = 1000000;
        int takeTimes = 1000000;
        EventBus bus = new CalculateEventBus(4);
        bus.addHandler(new SpeedHandler());
        bus.start();
        PoolContext poolContext = new PoolContext(count);
        List<EventContext> eventContexts = new ArrayList<EventContext>(takeTimes * 2);
        Timewatch timewatch = new Timewatch();
        timewatch.start();
        for (int i = 0; i < takeTimes; i++)
        {
            eventContexts.add(bus.post(poolContext, Speed.speed, "1"));
        }
        for (int i = 0; i < takeTimes; i++)
        {
            eventContexts.get(i).await();
        }
        timewatch.end();
        System.out.println(timewatch.getTotal());
    }
}
