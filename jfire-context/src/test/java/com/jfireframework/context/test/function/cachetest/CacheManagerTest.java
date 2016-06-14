package com.jfireframework.context.test.function.cachetest;

import javax.annotation.Resource;
import com.jfireframework.context.cache.Cache;
import com.jfireframework.context.cache.CacheManager;

@Resource
public class CacheManagerTest implements CacheManager
{
    
    private Cache cahce = new DemoCache();
    
    @Override
    public Cache get(String name)
    {
        System.out.println(name);
        return cahce;
    }
    
}
