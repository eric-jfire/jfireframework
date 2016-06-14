package com.jfireframework.context.test.function.cachetest;

import javax.annotation.Resource;
import com.jfireframework.context.cache.annotation.CacheDelete;
import com.jfireframework.context.cache.annotation.CacheGet;
import com.jfireframework.context.cache.annotation.CachePut;
import com.jfireframework.context.test.function.base.data.House;

@Resource
public class CacheTarget
{
    @CacheGet(key = "$id", cacheName = "name", condition = "$id > 4")
    public House get(int id)
    {
        System.out.println("调用");
        return new House();
    }
    
    @CachePut(key = "$id", cacheName = "name", condition = "$id > 2")
    public House put(int id)
    {
        return new House();
    }
    
    @CacheDelete(key = "$id", cacheName = "name")
    public void remove(int id)
    {
        ;
    }
}
