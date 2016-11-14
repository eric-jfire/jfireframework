package com.jfireframework.codejson.function.impl.write.extra;

import java.io.File;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.codejson.function.impl.write.WriterAdapter;
import com.jfireframework.codejson.tracker.Tracker;

public class FileWriter extends WriterAdapter
{
    @Override
    public void write(Object field, StringCache cache, Object entity, Tracker tracker)
    {
        cache.append('"').append(((File) field).getAbsolutePath()).append('"');
    }
}
