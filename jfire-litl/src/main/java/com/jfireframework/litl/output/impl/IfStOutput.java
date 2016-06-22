package com.jfireframework.litl.output.impl;

import java.util.Map;
import java.util.Queue;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.litl.output.impl.util.ParamType;
import com.jfireframework.litl.template.LineInfo;
import com.jfireframework.litl.template.Template;

public class IfStOutput extends IfCompareOutput
{
    
    public IfStOutput(String condition, LineInfo line, Queue<LineInfo> lineinfoQueue, Template template)
    {
        super(condition, "<", line, lineinfoQueue, template);
    }
    
    @Override
    public void output(StringCache cache, Map<String, Object> data)
    {
        Object target = data.get(varKeyForData);
        if (type == ParamType.INT)
        {
            if ((Integer) varAccess.getValue(target) < paramInteger)
            {
                content.output(cache, data);
            }
        }
        else
        {
            if ((Double) varAccess.getValue(target) < paramDouble)
            {
                content.output(cache, data);
            }
        }
    }
    
}
