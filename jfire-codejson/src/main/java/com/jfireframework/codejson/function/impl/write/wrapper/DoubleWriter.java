package com.jfireframework.codejson.function.impl.write.wrapper;

import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.codejson.function.impl.write.WriterAdapter;
import com.jfireframework.codejson.tracker.Tracker;

public class DoubleWriter extends WriterAdapter implements WrapperWriter
{
    
    @Override
    public void write(Object field, StringCache cache, Object entity,Tracker tracker)
    {
        cache.append((Double) field);
    }
    
}
