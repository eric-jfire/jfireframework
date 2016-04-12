package com.jfireframework.codejson.function.impl.write.array;

import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.codejson.function.impl.write.WriterAdapter;

public class CharArrayWriter extends WriterAdapter
{
    
    @Override
    public void write(Object field, StringCache cache, Object entity)
    {
        char[] array = (char[]) field;
        cache.append('[');
        for (char each : array)
        {
            cache.append('"').append(each).append("\",");
        }
        if (cache.isCommaLast())
        {
            cache.deleteLast();
        }
        cache.append(']');
    }
    
}
