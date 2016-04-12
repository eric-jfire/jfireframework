package com.jfireframework.codejson.function.impl.write.extra;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.codejson.function.impl.write.WriterAdapter;

public class DateWriter extends WriterAdapter
{
    private static ThreadLocal<SimpleDateFormat> formats = new ThreadLocal<SimpleDateFormat>() {
                                                             protected SimpleDateFormat initialValue()
                                                             {
                                                                 return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                             }
                                                         };
    
    @Override
    public void write(Object field, StringCache cache, Object entity)
    {
        cache.append('\"').append(formats.get().format((Date) field)).append('\"');
    }
    
}
