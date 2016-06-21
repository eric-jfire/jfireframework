package com.jfireframework.litl.output.impl;

import java.util.Map;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.litl.output.OutPut;

public class HtmlOutPut implements OutPut
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
    public void outputWithTempParam(StringCache cache, Map<String, Object> data)
    {
        // TODO Auto-generated method stub
        
    }
    
}
