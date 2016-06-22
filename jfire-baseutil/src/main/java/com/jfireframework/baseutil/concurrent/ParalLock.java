package com.jfireframework.baseutil.concurrent;

import java.util.concurrent.ConcurrentHashMap;

public class ParalLock
{
    private ConcurrentHashMap<String, Object> lockMap = new ConcurrentHashMap<String, Object>();
    
    public Object getLock(String key)
    {
        Object lock = lockMap.get(key);
        if (lock != null)
        {
            return lock;
        }
        lock = new Object();
        if (lockMap.putIfAbsent(key, lock) == null)
        {
            return lock;
        }
        else
        {
            return lockMap.get(key);
        }
    }
}
