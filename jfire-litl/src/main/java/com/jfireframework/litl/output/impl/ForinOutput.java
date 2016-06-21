package com.jfireframework.litl.output.impl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.litl.output.OutPutBuilder;
import com.jfireframework.litl.output.Output;
import com.jfireframework.litl.template.LineInfo;
import com.jfireframework.litl.template.Template;

public class ForinOutput implements Output
{
    private final String forin_each;
    private final String forin_array;
    private List<Output> outputs = new LinkedList<Output>();
    
    public ForinOutput(String method, LineInfo line, OutPutBuilder builder, Queue<LineInfo> lineinfoQueue, Template template)
    {
        int in_index = method.indexOf(" in ");
        Verify.True(in_index != -1, "for语句的表达式没有正确包含in。请检查模板：{}的第{}行", template.getPath(), line.getLine());
        int forin_end_index = method.indexOf("){", in_index);
        Verify.True(forin_end_index != -1, "for语句的表达式没有以){结束。请检查模板：{}的第{}行", template.getPath(), line.getLine());
        forin_each = method.substring(4, in_index);
        forin_array = method.substring(in_index + 4, forin_end_index);
        outputs.add(builder.build(lineinfoQueue, template));
    }
    
    @Override
    public void output(StringCache cache, Map<String, Object> data)
    {
        Object array = data.get(forin_array);
        if (array instanceof Collection<?>)
        {
            for (Object each : (Collection<?>) array)
            {
                data.put(forin_each, each);
                for (Output output : outputs)
                {
                    output.output(cache, data);
                }
            }
        }
        else if (array.getClass().isArray())
        {
            for (Object each : (Object[]) array)
            {
                data.put(forin_each, each);
                for (Output output : outputs)
                {
                    output.output(cache, data);
                }
            }
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
        // TODO Auto-generated method stub
        
    }
    
}
