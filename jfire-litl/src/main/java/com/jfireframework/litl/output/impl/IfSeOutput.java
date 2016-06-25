package com.jfireframework.litl.output.impl;

import java.util.Deque;
import java.util.Map;
import com.jfireframework.litl.output.impl.util.ParamType;
import com.jfireframework.litl.template.LineInfo;
import com.jfireframework.litl.template.Template;

public class IfSeOutput extends IfCompareOutput
{
    
    public IfSeOutput(String condition, LineInfo line, Deque<LineInfo> lineinfoQueue, Template template)
    {
        super(condition, " <= ", line, lineinfoQueue, template);
    }
    
    @Override
    protected boolean doIf(Map<String, Object> data)
    {
        Object target = data.get(varKeyForData);
        if (type == ParamType.INT)
        {
            if ((Integer) varAccess.getValue(target) <= paramInteger)
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
            if ((Double) varAccess.getValue(target) <= paramDouble)
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
