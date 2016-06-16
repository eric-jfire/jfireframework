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
        HashMapCache cache = new HashMapCache();
        cache.setName("default");
        cacheMap.put("default", cache);
    }
    
    @Override
    public Cache get(String name)
    {
        return cacheMap.get(name);
    }
    
}
