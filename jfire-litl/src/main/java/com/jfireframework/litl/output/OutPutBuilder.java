package com.jfireframework.litl.output;

import java.util.Deque;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.litl.TplCenter;
import com.jfireframework.litl.output.impl.CombinationOutput;
import com.jfireframework.litl.output.impl.ForinOutput;
import com.jfireframework.litl.output.impl.FunctionOutput;
import com.jfireframework.litl.output.impl.HtmlOutPut;
import com.jfireframework.litl.output.impl.IfEqualOutput;
import com.jfireframework.litl.output.impl.IfLeOutput;
import com.jfireframework.litl.output.impl.IfLtOutput;
import com.jfireframework.litl.output.impl.IfNonEqualOutput;
import com.jfireframework.litl.output.impl.IfSeOutput;
import com.jfireframework.litl.output.impl.IfStOutput;
import com.jfireframework.litl.output.impl.VarOutput;
import com.jfireframework.litl.template.LineInfo;
import com.jfireframework.litl.template.Template;

public class OutPutBuilder
{
    
    public static Output build(Deque<LineInfo> lineQueue, Template template)
    {
        TplCenter tplCenter = template.getTplCenter();
        StringCache htmlCache = new StringCache();
        Output result = new CombinationOutput();
        nextline: while (lineQueue.size() > 0)
        {
            LineInfo lineInfo = lineQueue.poll();
            String context = lineInfo.getContent();
            int index = 0;
            while (index < context.length())
            {
                char c = context.charAt(index);
                if (c == tplCenter.get_methodStartFlag() && context.indexOf(tplCenter.getMethodStartFlag(), index) == index)
                {
                    String append = htmlCache.toString().trim();
                    if (StringUtil.isNotBlank(append))
                    {
                        result.addOutput(new HtmlOutPut(append));
                    }
                    htmlCache.clear();
                    int end = context.indexOf(tplCenter.getMethodEndFlag(), index + tplCenter.getMethodStartFlag().length());
                    if (end == -1)
                    {
                        throw new UnSupportException(StringUtil.format("获取参数需要在一行内闭合，请检查第{}行", lineInfo.getLine()));
                    }
                    else
                    {
                        String method = context.substring(index + tplCenter.getMethodStartFlag().length(), end);
                        String leftLine = context.substring(end + tplCenter.getMethodEndFlag().length());
                        if (StringUtil.isNotBlank(leftLine.trim()))
                        {
                            LineInfo newline = new LineInfo(lineInfo.getLine(), leftLine);
                            lineQueue.addFirst(newline);
                        }
                        method = method.trim();
                        if (method.startsWith("for("))
                        {
                            result.addOutput(new ForinOutput(method, lineInfo, lineQueue, template));
                            continue nextline;
                        }
                        else if (method.startsWith("if("))
                        {
                            result.addOutput(handleIf(method, lineInfo, lineQueue, template));
                            continue nextline;
                        }
                        else if (method.equals("}"))
                        {
                            result.shirk();
                            return result;
                        }
                        else if (method.endsWith("{}"))
                        {
                            result.addOutput(new FunctionOutput(method, lineInfo, template));
                            continue nextline;
                        }
                        else
                        {
                            throw new UnSupportException(StringUtil.format("未识别的语法，请检查模板:{}第{}行", template.getPath(), lineInfo.getLine()));
                        }
                    }
                }
                if (c == tplCenter.get_varStartFlag() && context.indexOf(tplCenter.getVarStartFlag(), index) == index)
                {
                    String append = htmlCache.toString();
                    if (StringUtil.isNotBlank(append))
                    {
                        result.addOutput(new HtmlOutPut(append));
                    }
                    htmlCache.clear();
                    int end = context.indexOf(tplCenter.getVarEndFlag(), index + tplCenter.getVarStartFlag().length());
                    if (end == -1)
                    {
                        throw new UnSupportException(StringUtil.format("获取参数需要在一行内闭合，请检查第{}行", lineInfo.getLine()));
                    }
                    String var = context.substring(index + tplCenter.getVarStartFlag().length(), end).trim();
                    VarOutput varOutput = new VarOutput(var, lineInfo, template);
                    result.addOutput(varOutput);
                    index = end + tplCenter.getVarEndFlag().length();
                    continue;
                }
                htmlCache.append(c);
                index += 1;
            }
            htmlCache.append("\r\n");
            String append = htmlCache.toString();
            if (StringUtil.isNotBlank(append))
            {
                result.addOutput(new HtmlOutPut(append));
            }
            htmlCache.clear();
        }
        if (htmlCache.count() != 0)
        {
            String append = htmlCache.toString();
            if (StringUtil.isNotBlank(append))
            {
                result.addOutput(new HtmlOutPut(append));
            }
            htmlCache.clear();
        }
        result.shirk();
        return result;
    }
    
    private static Output handleIf(String method, LineInfo lineInfo, Deque<LineInfo> lineQueue, Template template)
    {
        if (method.contains(" == "))
        {
            return new IfEqualOutput(method, lineInfo, lineQueue, template);
        }
        else if (method.contains(" != "))
        {
            return new IfNonEqualOutput(method, lineInfo, lineQueue, template);
        }
        else if (method.contains(" > "))
        {
            return new IfLtOutput(method, lineInfo, lineQueue, template);
        }
        else if (method.contains(" >= "))
        {
            return new IfLeOutput(method, lineInfo, lineQueue, template);
        }
        else if (method.contains(" < "))
        {
            return new IfStOutput(method, lineInfo, lineQueue, template);
        }
        else if (method.contains(" <= "))
        {
            return new IfSeOutput(method, lineInfo, lineQueue, template);
        }
        else
        {
            throw new UnSupportException(StringUtil.format("无法确认的if语句类型。请检查模板:{}的第{}行", template.getPath(), lineInfo.getLine()));
        }
    }
}
