package com.jfireframework.litl.output.impl;

import java.util.Deque;
import java.util.Map;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.litl.output.OutPutBuilder;
import com.jfireframework.litl.output.Output;
import com.jfireframework.litl.output.impl.util.ParamType;
import com.jfireframework.litl.template.LineInfo;
import com.jfireframework.litl.template.Template;
import com.jfireframework.litl.varaccess.VarAccess;

public abstract class IfCompareOutput implements Output
{
    protected final ParamType type;
    protected String          paramString;
    protected Double          paramDouble;
    protected Integer         paramInteger;
    protected Boolean         paramBoolean;
    protected final VarAccess varAccess;
    protected final String    varKeyForData;
    protected final Output    content;
    protected Output          elseContent;
    
    public IfCompareOutput(String condition, String compare, LineInfo line, Deque<LineInfo> lineinfoQueue, Template template)
    {
        String var;
        if (condition.charAt(2) == ' ')
        {
            var = condition.substring(3, condition.indexOf(' ', 3));
        }
        else
        {
            var = condition.substring(3, condition.indexOf(' ', 2));
        }
        varKeyForData = var.split("\\.")[0];
        varAccess = new VarAccess(template.getPath(), var, line);
        int index = condition.indexOf(compare);
        int end = condition.indexOf("){", index);
        String condition_param = condition.substring(index + compare.length(), end);
        condition_param = condition_param.trim();
        if (condition_param.equals("null"))
        {
            type = ParamType.NULL;
        }
        else if (condition_param.startsWith("\"") && condition_param.endsWith("\""))
        {
            type = ParamType.STRING;
            paramString = condition_param.substring(1, condition_param.length() - 1);
        }
        else if (condition_param.equals("true") || condition_param.equals("false"))
        {
            type = ParamType.BOOLEAN;
            paramBoolean = Boolean.valueOf(condition_param);
        }
        else if (condition_param.indexOf('.') != -1)
        {
            type = ParamType.DOUBLE;
            paramDouble = Double.valueOf(condition_param);
        }
        else
        {
            type = ParamType.INT;
            paramInteger = Integer.valueOf(condition_param);
        }
        content = OutPutBuilder.build(lineinfoQueue, template);
        content.shirk();
        LineInfo lineInfo = lineinfoQueue.getFirst();
        int start = lineInfo.getContent().indexOf(template.getTplCenter().getMethodStartFlag());
        if (start != -1)
        {
            int elseIndex = lineInfo.getContent().indexOf("else{");
            end = lineInfo.getContent().indexOf(template.getTplCenter().getMethodEndFlag());
            if (elseIndex != -1 && start < elseIndex && elseIndex < end)
            {
                lineinfoQueue.poll();
                elseContent = OutPutBuilder.build(lineinfoQueue, template);
            }
            else
            {
                elseContent = null;
            }
        }
    }
    
    protected abstract boolean doIf(Map<String, Object> data);
    
    public void output(StringCache cache, Map<String, Object> data)
    {
        if (doIf(data))
        {
            content.output(cache, data);
        }
        if (elseContent != null)
        {
            elseContent.output(cache, data);
        }
    }
    
    @Override
    public void addOutput(Output outPut)
    {
        throw new UnsupportedOperationException();
    }
    
    public void shirk()
    {
        throw new UnsupportedOperationException();
    }
}
