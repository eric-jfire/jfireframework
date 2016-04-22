package com.jfireframework.codejson.function.impl.write.extra;

import java.util.ArrayList;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.codejson.function.WriterContext;
import com.jfireframework.codejson.function.impl.write.WriterAdapter;
import com.jfireframework.codejson.tracker.Tracker;

public class ArrayListWriter extends WriterAdapter
{
    
    @Override
    public void write(Object field, StringCache cache, Object entity, Tracker tracker)
    {
        ArrayList<?> list = (ArrayList<?>) field;
        cache.append('[');
        int size = list.size();
        Object value = null;
        for (int i = 0; i < size; i++)
        {
            if ((value = list.get(i)) != null)
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
