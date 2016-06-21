package com.jfireframework.litl.output.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.litl.output.Output;

public class CombinationOutput implements Output
{
    private List<Output> outputs = new LinkedList<Output>();
    
    @Override
    public void output(StringCache cache, Map<String, Object> data)
    {
        for (Output output : outputs)
        {
            output.output(cache, data);
        }
    }
    
    @Override
    public void outputWithTempParam(StringCache cache, Map<String, Object> data)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void addOutput(Output outPut)
    {
        outputs.add(outPut);
    }
    
}
