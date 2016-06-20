package com.jfireframework.context.cache.support;

import java.util.concurrent.ConcurrentHashMap;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.context.cache.Cache;

public class HashMapCache implements Cache
{
    private ConcurrentHashMap<Object, Object> map = new ConcurrentHashMap<Object, Object>();
    private String                            name;
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    @Override
    public void put(Object key, Object value)
    {
        map.put(key, value);
    }
    
    @Override
    public Object get(Object key)
    {
        return map.get(key);
    }
    
    @Override
    public void remove(Object key)
    {
        map.remove(key);
    }
    
    @Override
    public void clear()
    {
        map.clear();
    }
    
    @Override
    public String getName()
    {
        return name;
    }
    
    @Override
    public void put(Object key, Object value, int timeToLive)
    {
        throw new UnSupportException("该cache不支持设置缓存超时时间");
    }
    
}
