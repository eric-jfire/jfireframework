package com.jfireframework.litl.output;

import java.util.Map;
import com.jfireframework.baseutil.collection.StringCache;

public interface OutPut
{
    public void output(StringCache cache, Map<String, Object> data);
    
    public void outputWithTempParam(StringCache cache, Map<String, Object> data);
}
