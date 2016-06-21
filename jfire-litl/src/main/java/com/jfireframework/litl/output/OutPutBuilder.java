package com.jfireframework.litl.output;

import java.util.Queue;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.litl.TplCenter;
import com.jfireframework.litl.output.impl.CombinationOutput;
import com.jfireframework.litl.output.impl.ForinOutput;
import com.jfireframework.litl.output.impl.HtmlOutPut;
import com.jfireframework.litl.output.impl.IfEqualOutput;
import com.jfireframework.litl.output.impl.VarOutput;
import com.jfireframework.litl.template.LineInfo;
import com.jfireframework.litl.template.Template;

public class OutPutBuilder
{
    public Output build(Queue<LineInfo> lineQueue, Template template)
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
                    String append = htmlCache.toString();
                    result.addOutput(new HtmlOutPut(append));
                    htmlCache.clear();
                    int end = context.indexOf(tplCenter.getMethodEndFlag(), index + tplCenter.getMethodStartFlag().length());
                    if (end == -1)
                    {
                        throw new UnSupportException(StringUtil.format("获取参数需要在一行内闭合，请检查第{}行", lineInfo.getLine()));
                    }
                    else
                    {
                        String method = context.substring(index + tplCenter.getMethodStartFlag().length(), end);
                        method = method.trim();
                        if (method.startsWith("for("))
                        {
                            result.addOutput(new ForinOutput(method, lineInfo, this, lineQueue, template));
                            continue nextline;
                        }
                        else if (method.startsWith("if("))
                        {
                            if (method.contains(" == "))
                            {
                                
                            }
                            else if (method.contains(" != "))
                            {
                                
                            }
                            else if (method.contains(" > "))
                            {
                                
                            }
                            result.addOutput(new IfEqualOutput(method, lineInfo, this, lineQueue, template));
                            continue nextline;
                        }
                        else if (method.equals("}"))
                        {
                            return result;
                        }
                        else
                        {
                            throw new UnSupportException("未识别的语法");
                        }
                    }
                }
                if (c == tplCenter.get_varStartFlag() && context.indexOf(tplCenter.getVarStartFlag(), index) == index)
                {
                    String append = htmlCache.toString();
                    result.addOutput(new HtmlOutPut(append));
                    htmlCache.clear();
                    int end = context.indexOf(tplCenter.getVarEndFlag(), index + tplCenter.getVarStartFlag().length());
                    if (end == -1)
                    {
                        throw new UnSupportException(StringUtil.format("获取参数需要在一行内闭合，请检查第{}行", lineInfo.getLine()));
                    }
                    String var = context.substring(index + tplCenter.getVarStartFlag().length(), end);
                    var = var.trim();
                    VarOutput varOutput = new VarOutput(var, lineInfo);
                    result.addOutput(varOutput);
                    index = end + tplCenter.getVarEndFlag().length();
                    continue;
                }
                if (c == tplCenter.get_functionStartFlag() && context.indexOf(tplCenter.getFunctionStartFlag(), index) == index)
                {
                    int end = context.indexOf(tplCenter.getFunctionEndFlag(), index + tplCenter.getFunctionStartFlag().length());
                }
                htmlCache.append(c);
                index += 1;
            }
            htmlCache.append("\r\n");
        }
        return result;
    }
    
}
