package com.jfireframework.context.cache.support;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import com.jfireframework.context.cache.Cache;
import com.jfireframework.context.cache.CacheManager;

public class ConcurrentHashMapCacheManager implements CacheManager
{
    private Map<String, Cache> cacheMap = new HashMap<String, Cache>();
    
    @PostConstruct
    public void init()
    {
        cacheMap.put("default", new HashMapCache());
    }
    
    @Override
    public Cache get(String name)
    {
        return cacheMap.get(name);
    }
    
}
