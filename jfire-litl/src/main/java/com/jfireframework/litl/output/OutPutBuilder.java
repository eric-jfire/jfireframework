package com.jfireframework.litl.output;

import java.util.LinkedList;
import java.util.List;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.litl.TplCenter;
import com.jfireframework.litl.output.impl.HtmlOutPut;
import com.jfireframework.litl.template.LineInfo;
import com.jfireframework.litl.template.Template;

public class OutPutBuilder
{
    public void build(Template template, TplCenter tplCenter)
    {
        List<OutPut> outPuts = new LinkedList<OutPut>();
        LineInfo[] lineInfos = template.getContent();
        StringCache contextCache = new StringCache();
        skip: for (int i = 0; i < lineInfos.length; i++)
        {
            int index = 0;
            String context = lineInfos[i].getContent();
            while (index < context.length())
            {
                char c = context.charAt(index);
                if (c == tplCenter.get_methodStartFlag())
                {
                    if (context.indexOf(tplCenter.getMethodStartFlag(), index) == index)
                    {
                        String append = contextCache.toString().replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n");
                        outPuts.add(new HtmlOutPut(append));
                        contextCache.clear();
                        int end = context.indexOf(tplCenter.getMethodEndFlag(), index + tplCenter.getMethodStartFlag().length());
                        if (end == -1)
                        {
                            throw new UnSupportException(StringUtil.format("获取参数需要在一行内闭合，请检查第{}行", lineInfos[i].getLine()));
                        }
                        else
                        {
                            String method = context.substring(index + tplCenter.getMethodStartFlag().length(), end);
                            method = method.trim();
                            if (method.startsWith("for("))
                            {
                                
                            }
                            else if (method.startsWith("if("))
                            {
                                
                            }
                            else
                            {
                                throw new UnSupportException("未识别的语法");
                            }
                            continue;
                        }
                    }
                }
                if (c == tplCenter.get_varStartFlag())
                {
                    if (context.indexOf(tplCenter.getVarStartFlag(), index) == index)
                    {
                        contextCache.clear();
                        int end = context.indexOf(tplCenter.getVarEndFlag(), index + tplCenter.getVarStartFlag().length());
                    }
                }
                if (c == tplCenter.get_functionStartFlag())
                {
                    if (context.indexOf(tplCenter.getFunctionStartFlag(), index) == index)
                    {
                        int end = context.indexOf(tplCenter.getFunctionEndFlag(), index + tplCenter.getFunctionStartFlag().length());
                        
                    }
                }
            }
            index += 1;
        }
        contextCache.append("\r\n");
    }
    
    private outputResult buildFor(int start, String method, LineInfo[] lineInfos, Template template)
    {
        LineInfo line = lineInfos[start];
        int in_index = method.indexOf(" in ");
        Verify.True(in_index != -1, "for语句的表达式没有正确包含in。请检查模板：{}的第{}行", template.getPath(), line.getLine());
        int forin_end_index = method.indexOf("){", in_index);
        Verify.True(forin_end_index != -1, "for语句的表达式没有以){结束。请检查模板：{}的第{}行", template.getPath(), line.getLine());
        String forin_each = method.substring(4, in_index);
        String forin_array = method.substring(in_index + 4, forin_end_index);
        for (int i = start = 1; i < lineInfos.length; i++)
        {
            
        }
        return null;
    }
    
    class outputResult
    {
        int    line;
        OutPut outPut;
    }
}
