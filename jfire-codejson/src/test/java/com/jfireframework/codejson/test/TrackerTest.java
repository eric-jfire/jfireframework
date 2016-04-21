package com.jfireframework.codejson.test;

import org.junit.Test;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.codejson.function.WriteStrategy;
import com.jfireframework.codejson.function.impl.write.WriterAdapter;
import com.jfireframework.codejson.test.simple.Guy;
import com.jfireframework.codejson.test.simple.Room;
import com.jfireframework.codejson.tracker.Tracker;

public class TrackerTest
{
    @Test
    public void test()
    {
        Room room = new Room();
        room.setLength(100);
        Guy guy = new Guy();
        guy.setName("sadasd");
        guy.setRoom(room);
        room.setGuy(guy);
        WriteStrategy strategy = new WriteStrategy();
        strategy.setUseTracker(true);
        strategy.addTrackerType(Guy.class, new WriterAdapter() {
            @Override
            public void write(Object field, StringCache cache, Object entity, Tracker tracker)
            {
                Guy guy = (Guy) field;
                String path = tracker.getPath(field);
                cache.append("{\"$ref\":").append(path).append(",\"我想输出什么都可以\":\"").append(guy.getName()).append("\"}");
                System.err.println(tracker.getPath(field));
            }
        });
        System.out.println(strategy.write(guy));
    }
}
