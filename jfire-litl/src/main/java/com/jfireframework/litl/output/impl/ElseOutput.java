package com.jfireframework.litl.output.impl;

import java.util.Deque;
import java.util.Map;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.litl.output.OutPutBuilder;
import com.jfireframework.litl.output.Output;
import com.jfireframework.litl.template.LineInfo;
import com.jfireframework.litl.template.Template;

public class ElseOutput implements Output
{
    private Output content;
    
    public ElseOutput(Deque<LineInfo> lineInfos, Template template, LineInfo info)
    {
        content = OutPutBuilder.build(lineInfos, template);
    }
    
    @Override
    public void output(StringCache cache, Map<String, Object> data)
    {
        content.output(cache, data);
    }
    
    @Override
    public void addOutput(Output outPut)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void shirk()
    {
        // TODO Auto-generated method stub
        
    }
    
}
