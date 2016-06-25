package com.jfireframework.litl.output.impl;

import java.util.Deque;
import java.util.Map;
import com.jfireframework.litl.output.Output;
import com.jfireframework.litl.output.impl.util.ParamType;
import com.jfireframework.litl.template.LineInfo;
import com.jfireframework.litl.template.Template;

public class IfLtOutput extends IfCompareOutput
{
    
    public IfLtOutput(String condition, LineInfo line, Deque<LineInfo> lineinfoQueue, Template template)
    {
        super(condition, " > ", line, lineinfoQueue, template);
    }
    
    @Override
    public void addOutput(Output outPut)
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected boolean doIf(Map<String, Object> data)
    {
        Object target = data.get(varKeyForData);
        if (type == ParamType.INT)
        {
            if ((Integer) varAccess.getValue(target) > paramInteger)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            if ((Double) varAccess.getValue(target) > paramDouble)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    }
    
}
