package com.jfireframework.eventbus;

import org.junit.Test;
import com.jfireframework.eventbus.bus.EventBus;

public class PrintTest
{
    @Test
    public void test() throws InterruptedException
    {
        EventBus bus = new EventBus(128);
        bus.addHandler(new PrintHandler());
        bus.start();
        bus.post(null, Print.one);
        Thread.sleep(1000);
    }
}
