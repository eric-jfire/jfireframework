package com.jfireframework.eventbus.readwrite;

import org.junit.Test;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.bus.impl.CalculateEventBus;

public class ReadWriteTest
{
    @Test
    public void test()
    {
        EventBus bus = new CalculateEventBus(4);
        bus.addHandler(new ReadHandler());
        bus.addHandler(new WriteHandler());
        bus.start();
        bus.post("读取1", ReadWriteEvent.read);
        bus.post("读取2", ReadWriteEvent.read);
        bus.post("读取3", ReadWriteEvent.read);
        bus.post("写出1", ReadWriteEvent.write);
        bus.post("读取4", ReadWriteEvent.read);
        bus.post("读取5", ReadWriteEvent.read);
        bus.post("写出2", ReadWriteEvent.write);
        bus.post("读取6", ReadWriteEvent.read);
        bus.post("读取7", ReadWriteEvent.read).await();
        ;
    }
}
