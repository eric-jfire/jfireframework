package com.jfireframework.codejson.function.impl.write;

import java.util.Iterator;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.codejson.function.WriterContext;

public class IteratorWriter extends WriterAdapter
{
    
    @SuppressWarnings("rawtypes")
    @Override
    public void write(Object field, StringCache cache, Object entity)
    {
        cache.append('[');
        Iterator it = ((Iterable) field).iterator();
        Object value = null;
        while (it.hasNext())
        {
            if ((value = it.next()) != null)
            {
                if (value instanceof String)
                {
                    cache.append('"').append((String) value).append('"');
                }
                else
                {
                    WriterContext.write(value, cache);
                }
                cache.append(',');
            }
        }
        if (cache.isCommaLast())
        {
            cache.deleteLast();
        }
        cache.append(']');
    }
    
}
