package com.jfireframework.baseutil.test;

import org.junit.Test;
import com.jfireframework.baseutil.Timelog;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;

public class SpeedTest
{
    private Logger logger = ConsoleLogFactory.getLogger(ConsoleLogFactory.DEBUG);
    
    @Test
    public void StringBuilderAndStringCache()
    {
        int count = 1000000;
        Timelog timelog = new Timelog();
        StringBuilder builder = new StringBuilder(2 * count);
        StringCache cache = new StringCache(2 * count);
        timelog.start();
        for (int i = 0; i < count; i++)
        {
            cache.append("你好");
        }
        cache.toString();
        timelog.end();
        logger.debug("cache使用的时间是{}", timelog.total());
        timelog.start();
        for (int i = 0; i < count; i++)
        {
            builder.append("你好");
        }
        cache.toString();
        timelog.end();
        logger.debug("builder使用的时间是{}", timelog.total());
    }
}
