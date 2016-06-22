package com.jfireframework.litl.output.impl;

import java.util.Map;
import java.util.Queue;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.litl.output.OutPutBuilder;
import com.jfireframework.litl.output.Output;
import com.jfireframework.litl.output.impl.util.ParamType;
import com.jfireframework.litl.template.LineInfo;
import com.jfireframework.litl.template.Template;
import com.jfireframework.litl.varaccess.VarAccess;

public class IfEqualOrNonequalOutput implements Output
{
    private Output          content;
    private VarAccess       varAccess = new VarAccess();
    private final String    var;
    private final ParamType type;
    private String          paramString;
    private Double          paramDouble;
    private Integer         paramInteger;
    private Boolean         paramBoolean;
    private boolean         equal     = false;
    
    public IfEqualOrNonequalOutput(String condition, LineInfo line, Queue<LineInfo> lineinfoQueue, Template template)
    {
        if (condition.charAt(2) == ' ')
        {
            var = condition.substring(3, condition.indexOf(' ', 3));
        }
        else
        {
            var = condition.substring(3, condition.indexOf(' ', 2));
        }
        if (condition.contains(" == "))
        {
            equal = true;
        }
        else
        {
            equal = false;
        }
        int index = equal ? condition.indexOf(" == ") : condition.indexOf(" != ");
        int end = condition.indexOf("){", index);
        String condition_param = condition.substring(index + 4, end);
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
    }
    
    @Override
    public void output(StringCache cache, Map<String, Object> data)
    {
        switch (type)
        {
            case NULL:
                if (equal == (varAccess.getValue(var, var, data.get(var), 0) == null))
                {
                    break;
                }
                else
                {
                    return;
                }
            case STRING:
                if (equal == (paramString.equals(varAccess.getValue(var, var, data.get(var), 0))))
                {
                    break;
                }
                else
                {
                    return;
                }
            case DOUBLE:
                if (equal == (paramDouble.equals(varAccess.getValue(var, var, data.get(var), 0))))
                {
                    break;
                }
                else
                {
                    return;
                }
            case INT:
                if (equal == (paramInteger.equals(varAccess.getValue(var, var, data.get(var), 0))))
                {
                    break;
                }
                else
                {
                    return;
                }
            case BOOLEAN:
                if (equal == (paramBoolean.equals(varAccess.getValue(var, var, data, 0))))
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
