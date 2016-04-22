package com.jfireframework.codejson.function.impl.write;

import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.codejson.function.JsonWriter;
import com.jfireframework.codejson.tracker.Tracker;

public abstract class WriterAdapter implements JsonWriter
{
    
    @Override
    public void write(Object field, StringCache cache, Object entity, Tracker tracker)
    {
        throw new RuntimeException("没有实现");
    }
    
    @Override
    public void write(int field, StringCache cache, Object entity)
    {
        throw new RuntimeException("没有实现");
    }
    
    @Override
    public void write(float field, StringCache cache, Object entity)
    {
        throw new RuntimeException("没有实现");
    }
    
    @Override
    public void write(double target, StringCache cache, Object entity)
    {
        throw new RuntimeException("没有实现");
    }
    
    @Override
    public void write(long field, StringCache cache, Object entity)
    {
        throw new RuntimeException("没有实现");
    }
    
    @Override
    public void write(byte field, StringCache cache, Object entity)
    {
        throw new RuntimeException("没有实现");
    }
    
    @Override
    public void write(char field, StringCache cache, Object entity)
    {
        throw new RuntimeException("没有实现");
    }
    
    @Override
    public void write(short field, StringCache cache, Object entity)
    {
        throw new RuntimeException("没有实现");
    }
    
    @Override
    public void write(boolean field, StringCache cache, Object entity)
    {
        throw new RuntimeException("没有实现");
    }
    
}
