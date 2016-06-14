package com.jfireframework.context.cache;

public interface Cache
{
    public void put(Object key, Object value);
    
    public Object get(Object key);
    
    public void remove(Object key);
    
    public void clear();
    
    public String getName();
}
