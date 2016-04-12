package com.jfireframework.codejson.function.impl.write.wrapper;

import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.codejson.function.impl.write.WriterAdapter;

public class StringWriter extends WriterAdapter implements WrapperWriter
{
    
    public void write(Object field, StringCache cache, Object entity)
    {
        cache.append('\"').append((String) field).append('\"');
    }
    
}
