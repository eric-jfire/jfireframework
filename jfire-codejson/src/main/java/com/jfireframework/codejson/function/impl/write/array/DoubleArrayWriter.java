package com.jfireframework.codejson.function.impl.write.array;

import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.codejson.function.impl.write.WriterAdapter;
import com.jfireframework.codejson.tracker.Tracker;

public class DoubleArrayWriter extends WriterAdapter
{
    @Override
    public void write(Object field, StringCache cache, Object entity, Tracker tracker)
    {
        double[] array = (double[]) field;
        cache.append('[');
        for (double each : array)
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
