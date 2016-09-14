package com.jfireframework.baseutil.autonomydisruptor;

import org.junit.Test;

public class AutoTest
{
    @Test
    public void test() throws InterruptedException
    {
        AutonomyDisruptor disruptor = new AutonomyDisruptor(
                4, new EntryActionFactory() {
                    
                    @Override
                    public AutonomyEntryAction newEntryAction(AutonomyRingArray ringArray, long cursor)
                    {
                        return new PrintAction(ringArray, cursor, 5);
                    }
                }
        );
        disruptor.publish("你好1");
        disruptor.publish("你好2");
        disruptor.publish("你好3");
        disruptor.publish("你好4");
        disruptor.publish("你好5");
        disruptor.publish("你好6");
        disruptor.publish("你好7");
        disruptor.publish("你好8");
        disruptor.publish("你好9");
        disruptor.publish("你好10");
        disruptor.publish("你好11");
        Thread.sleep(1000000);
    }
}
