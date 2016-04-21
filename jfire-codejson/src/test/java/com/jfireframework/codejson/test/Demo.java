package com.jfireframework.codejson.test;

import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.codejson.function.JsonWriter;
import com.jfireframework.codejson.function.WriteStrategy;
import com.jfireframework.codejson.tracker.Tracker;

public class Demo implements JsonWriter
{
private WriteStrategy writeStrategy;
    @Override
    public void write(Object field, StringCache cache, Object entity, Tracker tracker)
    {
        com.jfireframework.codejson.test.strategy.FunctionData8 entity30912488646488 =(com.jfireframework.codejson.test.strategy.FunctionData8 )field;
        cache.append('{');
        java.lang.Object data = entity30912488646488.getData();
        if(data!=null)
        {
            cache.append("\"data\":");
            JsonWriter writer = writeStrategy.getWriter(data.getClass());
            writer.write(data,cache,entity30912488646488,null);
            cache.append(',');
        }
        if(cache.isCommaLast())
        {
            cache.deleteLast();
        }
        cache.append('}');
    }

    @Override
    public void write(int field, StringCache cache, Object entity)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void write(float field, StringCache cache, Object entity)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void write(double field, StringCache cache, Object entity)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void write(long field, StringCache cache, Object entity)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void write(byte field, StringCache cache, Object entity)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void write(char field, StringCache cache, Object entity)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void write(short field, StringCache cache, Object entity)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void write(boolean field, StringCache cache, Object entity)
    {
        // TODO Auto-generated method stub
        
    }
    
}
