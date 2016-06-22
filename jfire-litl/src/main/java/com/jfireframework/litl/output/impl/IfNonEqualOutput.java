package com.jfireframework.litl.output.impl;

import java.util.Deque;
import java.util.Map;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.litl.output.Output;
import com.jfireframework.litl.template.LineInfo;
import com.jfireframework.litl.template.Template;

public class IfNonEqualOutput extends IfCompareOutput
{
    
    public IfNonEqualOutput(String condition, LineInfo line, Deque<LineInfo> lineinfoQueue, Template template)
    {
        super(condition, " != ", line, lineinfoQueue, template);
    }
    
    @Override
    public void output(StringCache cache, Map<String, Object> data)
    {
        Object target = data.get(varKeyForData);
        switch (type)
        {
            case NULL:
                if (varAccess.getValue(target) != null)
                {
                    break;
                }
                else
                {
                    return;
                }
            case STRING:
                if (paramString.equals(varAccess.getValue(target)) == false)
                {
                    break;
                }
                else
                {
                    return;
                }
            case DOUBLE:
                if (paramDouble.equals(varAccess.getValue(target)) == false)
                {
                    break;
                }
                else
                {
                    return;
                }
            case INT:
                if (paramInteger.equals(varAccess.getValue(target)) == false)
                {
                    break;
                }
                else
                {
                    return;
                }
            case BOOLEAN:
                if (paramBoolean.equals(varAccess.getValue(target)) == false)
                {
                    break;
                }
                else
                {
                    return;
                }
        }
        content.output(cache, data);
    }
    
    @Override
    public void addOutput(Output outPut)
    {
        throw new UnsupportedOperationException();
    }
    
}
