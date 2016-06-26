package com.jfireframework.litl.output.impl;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.el.JelExplain;
import com.jfireframework.litl.output.OutPutBuilder;
import com.jfireframework.litl.output.Output;
import com.jfireframework.litl.output.impl.util.ParamType;
import com.jfireframework.litl.template.LineInfo;
import com.jfireframework.litl.template.Template;
import com.jfireframework.litl.varaccess.VarAccess;

public abstract class IfCompareOutput implements Output
{
    protected final ParamType   type;
    protected String            paramString;
    protected Double            paramDouble;
    protected Integer           paramInteger;
    protected Boolean           paramBoolean;
    protected final VarAccess   varAccess;
    protected final String      varKeyForData;
    protected final Output      content;
    protected Output            elseContent;
    protected IfCompareOutput[] elseIfs;
    
    public IfCompareOutput(String condition, String compare, LineInfo line, Deque<LineInfo> lineinfoQueue, Template template)
    {
        String var;
        int paramStart = condition.indexOf('(') + 1;
        int paramEnd = JelExplain.getEndFlag(condition, paramStart);
        var = condition.substring(paramStart, paramEnd);
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
        List<IfCompareOutput> elseIfList = new LinkedList<IfCompareOutput>();
        while (true)
        {
            LineInfo lineInfo = lineinfoQueue.getFirst();
            String content = lineInfo.getContent();
            boolean detectElse = content.trim().startsWith(template.getTplCenter().getMethodStartFlag());
            if (detectElse)
            {
                int start = content.indexOf(template.getTplCenter().getMethodStartFlag());
                int end_1 = content.indexOf(template.getTplCenter().getMethodEndFlag(), start);
                String method = content.substring(start, end_1);
                int elseIfIndex = method.indexOf("else if(");
                int elseIndex = method.indexOf("else{");
                if (elseIfIndex != -1 || elseIndex != -1)
                {
                    lineInfo = lineinfoQueue.poll();
                    String leftLine = content.substring(end_1 + template.getTplCenter().getMethodEndFlag().length());
                    if (StringUtil.isNotBlank(leftLine.trim()))
                    {
                        LineInfo newline = new LineInfo(lineInfo.getLine(), leftLine);
                        lineinfoQueue.addFirst(newline);
                    }
                    method = method.trim();
                    if (elseIfIndex != -1)
                    {
                        elseIfList.add((IfCompareOutput) OutPutBuilder.handleIf(method, lineInfo, lineinfoQueue, template));
                        continue;
                    }
                    else
                    {
                        elseContent = OutPutBuilder.build(lineinfoQueue, template);
                    }
                }
                else
                {
                    elseContent = null;
                    break;
                }
            }
            else
            {
                break;
            }
        }
        elseIfs = elseIfList.toArray(new IfCompareOutput[elseIfList.size()]);
    }
    
    protected abstract boolean doIf(Map<String, Object> data);
    
    public void output(StringCache cache, Map<String, Object> data)
    {
        if (doIf(data))
        {
            content.output(cache, data);
        }
        boolean hitIf = false;
        for (IfCompareOutput each : elseIfs)
        {
            if (each.doIf(data))
            {
                hitIf = true;
                each.output(cache, data);
                break;
            }
        }
        if (hitIf)
        {
            return;
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
