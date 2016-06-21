package com.jfireframework.litl.output.impl;

import java.util.Map;
import java.util.Queue;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.litl.output.OutPutBuilder;
import com.jfireframework.litl.output.Output;
import com.jfireframework.litl.template.LineInfo;
import com.jfireframework.litl.template.Template;
import com.jfireframework.litl.varaccess.VarAccess;

public class IfEqualOutput implements Output
{
    private Output       content;
    private VarAccess    varAccess   = new VarAccess();
    private final String param;
    private boolean      equalNull   = false;
    private boolean      equalString = false;
    private String       equalStringTarget;
    private boolean      equalInt    = false;
    private boolean      equalDouble = false;
    private Double       dnum;
    private Integer      inum;
    
    public IfEqualOutput(String condition, LineInfo line, OutPutBuilder builder, Queue<LineInfo> lineinfoQueue, Template template)
    {
        if (condition.charAt(2) == ' ')
        {
            param = condition.substring(3, condition.indexOf(' ', 3));
        }
        else
        {
            param = condition.substring(3, condition.indexOf(' ', 2));
        }
        int index = condition.indexOf(" == ");
        int end = condition.indexOf("){", index);
        String condition_param = condition.substring(index + 4, end);
        condition_param = condition_param.trim();
        if (condition_param.equals("null"))
        {
            equalNull = true;
        }
        else if (condition_param.startsWith("\"") && condition_param.endsWith("\""))
        {
            equalString = true;
            equalStringTarget = condition_param.substring(1, condition_param.length() - 1);
        }
        else if (condition_param.indexOf('.') != -1)
        {
            equalDouble = true;
            dnum = Double.valueOf(condition_param);
        }
        else
        {
            equalInt = true;
            inum = Integer.valueOf(condition_param);
        }
        content = builder.build(lineinfoQueue, template);
    }
    
    @Override
    public void output(StringCache cache, Map<String, Object> data)
    {
        if (equalNull)
        {
            if (varAccess.getValue(param, param, data.get(param), 0) != null)
            {
                content.output(cache, data);
            }
        }
        else if (equalString)
        {
            if (equalStringTarget.equals(varAccess.getValue(param, param, data.get(param), 0)))
            {
                content.output(cache, data);
            }
        }
        else if (equalDouble)
        {
            if (dnum.equals(varAccess.getValue(param, param, data.get(param), 0)))
            {
                content.output(cache, data);
            }
        }
        else
        {
            if (inum.equals(varAccess.getValue(param, param, data.get(param), 0)))
            {
                content.output(cache, data);
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
