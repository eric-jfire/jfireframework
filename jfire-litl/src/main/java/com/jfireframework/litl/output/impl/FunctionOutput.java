package com.jfireframework.litl.output.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.litl.function.Function;
import com.jfireframework.litl.function.FunctionRegister;
import com.jfireframework.litl.output.Output;
import com.jfireframework.litl.template.LineInfo;
import com.jfireframework.litl.template.Template;

public class FunctionOutput implements Output
{
    private final Function function;
    
    public FunctionOutput(String function, LineInfo lineInfo, Template template)
    {
        try
        {
            int start = function.indexOf('(');
            String functionName = function.substring(0, start);
            int end = function.lastIndexOf("{}") - 1;
            String[] params = function.substring(start + 1, end).trim().split(",");
            List<Object> tmp = new LinkedList<Object>();
            for (String param : params)
            {
                if (param.charAt(0) == '"')
                {
                    Verify.True(param.charAt(param.length() - 1) == '"', "字符串参数需要完整闭合，请检查模板{}的第{}行", template.getPath(), lineInfo.getLine());
                    tmp.add(param.substring(1, param.length() - 1));
                    continue;
                }
                else if (param.equals("true"))
                {
                    tmp.add(true);
                }
                else if (param.equals("false"))
                {
                    tmp.add(false);
                }
                else if (param.equals("null"))
                {
                    tmp.add(null);
                }
                else if (param.contains("\\."))
                {
                    tmp.add(Double.valueOf(param));
                }
                else
                {
                    tmp.add(Integer.valueOf(param));
                }
            }
            Object[] functionParams = tmp.toArray();
            this.function = FunctionRegister.get(functionName, functionParams, lineInfo, template);
        }
        catch (Exception e)
        {
            throw new UnSupportException(StringUtil.format("创建模板异常，请检查模板:{}的第{}行", template.getPath(), lineInfo.getLine()), e);
        }
    }
    
    @Override
    public void output(StringCache cache, Map<String, Object> data)
    {
        function.call(data, cache);
    }
    
    @Override
    public void addOutput(Output outPut)
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void shirk()
    {
        
    }
    
}
