package com.jfireframework.codejson.function.impl.write;

import java.util.Map;
import java.util.Map.Entry;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.codejson.function.JsonWriter;
import com.jfireframework.codejson.function.WriteStrategy;
import com.jfireframework.codejson.function.impl.write.wrapper.StringWriter;
import java.util.Set;

public class StrategyMapWriter extends WriterAdapter
{
    private WriteStrategy strategy;
    private JsonWriter    stringWriter;
    
    public StrategyMapWriter(WriteStrategy strategy)
    {
        this.strategy = strategy;
        stringWriter = strategy.getWriter(String.class);
        if (stringWriter instanceof StringWriter)
        {
            stringWriter = null;
        }
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void write(Object field, StringCache cache, Object entity)
    {
        cache.append('{');
        Set<Entry> set = ((Map) field).entrySet();
        for (Entry each : set)
        {
            if (each.getKey() != null && each.getValue() != null)
            {
                
                if (each.getKey() instanceof String)
                {
                    if (stringWriter == null)
                    {
                        cache.append('"').append((String) each.getKey()).append("\":");
                    }
                    else
                    {
                        stringWriter.write(each.getKey(), cache, entity);
                        cache.append(':');
                    }
                }
                else
                {
                    cache.append('"');
                    strategy.getWriter(each.getKey().getClass()).write(each.getKey(), cache, entity);
                    cache.append("\":");
                }
                if (each.getValue() instanceof String)
                {
                    if (stringWriter == null)
                    {
                        cache.append('"').append((String) each.getValue()).append('"');
                    }
                    else
                    {
                        stringWriter.write(each.getKey(), cache, entity);
                    }
                }
                else
                {
                    strategy.getWriter(each.getValue().getClass()).write(each.getValue(), cache, entity);
                }
                cache.append(',');
            }
        }
        if (cache.isCommaLast())
        {
            cache.deleteLast();
        }
        cache.append('}');
    }
}
