package com.jfireframework.context.test.function.cachetest;

import org.junit.Assert;
import org.junit.Test;
import com.jfireframework.context.JfireContext;
import com.jfireframework.context.JfireContextImpl;
import com.jfireframework.context.test.function.base.data.House;

public class CacheTest
{
    @Test
    public void test()
    {
        JfireContext jfireContext = new JfireContextImpl();
        jfireContext.addBean(CacheTarget.class);
        jfireContext.addBean(DemoCache.class);
        jfireContext.addBean(CacheManagerTest.class);
        CacheTarget cacheTarget = jfireContext.getBean(CacheTarget.class);
        House house = cacheTarget.get(1);
        House second = cacheTarget.get(1);
        Assert.assertFalse(house == second);
        house = cacheTarget.get(5);
        second = cacheTarget.get(5);
        Assert.assertTrue(house == second);
        cacheTarget.put(5);
        second = cacheTarget.get(5);
        Assert.assertFalse(house == second);
        house = cacheTarget.get(5);
        second = cacheTarget.get(5);
        Assert.assertTrue(house == second);
        cacheTarget.remove(5);
        second = cacheTarget.get(5);
        Assert.assertFalse(house == second);
        String first = cacheTarget.get();
        String seconde = cacheTarget.get();
        Assert.assertTrue(first == seconde);
        cacheTarget.put();
        seconde = cacheTarget.get();
        Assert.assertFalse(first == seconde);
        first = cacheTarget.get();
        Assert.assertTrue(first == seconde);
        
    }
}
