package com.jfireframework.context.test.function.cachetest;

import org.junit.Test;
import com.jfireframework.context.JfireContext;
import com.jfireframework.context.JfireContextImpl;
import com.jfireframework.context.test.function.base.data.House;

public class NewCacheTest
{
    @Test
    public void test()
    {
        JfireContext jfireContext = new JfireContextImpl();
        jfireContext.addBean(CacheTarget.class);
        jfireContext.addBean(DemoCache.class);
        jfireContext.addBean(CacheManagerTest.class);
        CacheTarget cacheTarget = jfireContext.getBean(CacheTarget.class);
        House house = cacheTarget.get(5);
        System.out.println(house);
        house = cacheTarget.get(5);
        System.out.println(house);
        
    }
}
