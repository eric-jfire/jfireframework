package com.jfireframework.eventbus;

import org.junit.Test;
import com.jfireframework.eventbus.bus.EventBusImpl;

public class PrintTest
{
    @Test
    public void test() throws InterruptedException
    {
        EventBusImpl bus = new EventBusImpl(128);
        bus.addHandler(new PrintHandler());
        bus.start();
        bus.post(null, Print.one);
        Thread.sleep(1000);
    }
}
