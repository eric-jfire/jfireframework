package com.jfireframework.litl.output.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.litl.output.Output;

public class CombinationOutput implements Output
{
    private List<Output> tmp = new LinkedList<Output>();
    private Output[]     outputs;
    
    @Override
    public void output(StringCache cache, Map<String, Object> data)
    {
        for (Output output : outputs)
        {
            output.output(cache, data);
        }
    }
    
    @Override
    public void addOutput(Output outPut)
    {
        if (outPut instanceof CombinationOutput)
        {
            if (((CombinationOutput) outPut).getOutputs().length == 0)
            {
                return;
            }
        }
        tmp.add(outPut);
    }
    
    @Override
    public void shirk()
    {
        outputs = tmp.toArray(new Output[tmp.size()]);
    }
    
    public Output[] getOutputs()
    {
        return outputs;
    }
    
}
