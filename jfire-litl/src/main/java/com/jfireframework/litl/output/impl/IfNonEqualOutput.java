package com.jfireframework.litl.output.impl;

import java.util.Deque;
import java.util.Map;
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
    public void addOutput(Output outPut)
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected boolean doIf(Map<String, Object> data)
    {
        Object target = data.get(varKeyForData);
        switch (type)
        {
            case NULL:
                if (varAccess.getValue(target) != null)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            case STRING:
                if (paramString.equals(varAccess.getValue(target)) == false)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            case DOUBLE:
                if (paramDouble.equals(varAccess.getValue(target)) == false)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            case INT:
                if (paramInteger.equals(varAccess.getValue(target)) == false)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            case BOOLEAN:
                if (paramBoolean.equals(varAccess.getValue(target)) == false)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            default:
                return false;
        }
    }
    
}
