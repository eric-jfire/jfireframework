package com.jfireframework.codejson.function.impl.write;

import java.util.Map;
import java.util.Map.Entry;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.codejson.function.WriterContext;
import java.util.Set;

public class MapWriter extends WriterAdapter
{
    
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
                    cache.append('"').append((String) each.getKey()).append("\":");
                }
                else
                {
                    cache.append('"');
                    WriterContext.write(each.getKey(), cache);
                    cache.append("\":");
                }
                if (each.getValue() instanceof String)
                {
                    cache.append('"').append((String) each.getValue()).append('"');
                }
                else
                {
                    WriterContext.write(each.getValue(), cache);
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
