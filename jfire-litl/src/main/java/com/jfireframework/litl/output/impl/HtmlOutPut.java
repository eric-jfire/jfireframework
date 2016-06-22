package com.jfireframework.litl.output.impl;

import java.util.Map;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.litl.output.Output;

public class HtmlOutPut implements Output
{
    private final String content;
    
    public HtmlOutPut(String content)
    {
        this.content = content;
    }
    
    @Override
    public void output(StringCache cache, Map<String, Object> data)
    {
        cache.append(content);
    }
    
    @Override
    public void addOutput(Output outPut)
    {
        throw new UnSupportException("");
    }

    @Override
    public void shirk()
    {
        
    }
    
}
