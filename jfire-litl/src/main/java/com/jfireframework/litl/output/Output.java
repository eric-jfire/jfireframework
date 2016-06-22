package com.jfireframework.litl.output;

import java.util.Map;
import com.jfireframework.baseutil.collection.StringCache;

public interface Output
{
    public void output(StringCache cache, Map<String, Object> data);
    
    public void addOutput(Output outPut);
}
