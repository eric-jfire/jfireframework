package com.jfireframework.codejson.function.impl.write.array;

import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.codejson.function.impl.write.WriterAdapter;

public class BooleanArrayWriter extends WriterAdapter
{
    
    @Override
    public void write(Object field, StringCache cache, Object entity)
    {
        boolean[] array = (boolean[]) field;
        cache.append('[');
        for (boolean each : array)
        {
            cache.append(each).append(',');
        }
        if (cache.isCommaLast())
        {
            cache.deleteLast();
        }
        cache.append(']');
    }
    
}
