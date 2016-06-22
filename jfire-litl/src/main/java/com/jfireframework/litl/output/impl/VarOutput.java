package com.jfireframework.litl.output.impl;

import java.util.Map;
import java.util.Map.Entry;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.litl.format.Format;
import com.jfireframework.litl.format.NameFormatRegister;
import com.jfireframework.litl.format.impl.TypeFormat;
import com.jfireframework.litl.output.Output;
import com.jfireframework.litl.template.LineInfo;
import com.jfireframework.litl.template.Template;
import com.jfireframework.litl.varaccess.VarAccess;

public class VarOutput implements Output
{
    private final String    varName;
    private final VarAccess varAccess;
    private Format          format;
    private final boolean   isFormat;
    
    public VarOutput(String method, LineInfo info, Template template)
    {
        String var;
        if (method.contains(","))
        {
            String[] spl = method.split(",");
            varName = spl[0].split("\\.")[0];
            var = spl[0];
            String pattern = spl[1];
            if (pattern.charAt(0) == '"')
            {
                format = new TypeFormat();
                format.setPattern(pattern.substring(1, pattern.length() - 1).trim());
            }
            else
            {
                for (Entry<String, Class<? extends Format>> entry : NameFormatRegister.getFormats().entrySet())
                {
                    if (pattern.startsWith(entry.getKey()) && pattern.contains("="))
                    {
                        // +1是因为有等于号
                        pattern = pattern.substring(entry.getKey().length() + 2, pattern.length() - 1).trim();
                        try
                        {
                            format = entry.getValue().newInstance();
                        }
                        catch (Exception e)
                        {
                            throw new JustThrowException(e);
                        }
                        format.setPattern(pattern);
                        break;
                    }
                }
                if (format == null)
                {
                    throw new UnSupportException(StringUtil.format("未识别的格式化方法，请检查模板:{}第{}行", template.getPath(), info.getLine()));
                }
            }
            isFormat = true;
        }
        else
        {
            String[] tmp = method.split("\\.");
            varName = tmp[0];
            var = method;
            isFormat = false;
        }
        varAccess = new VarAccess(template.getPath(), var, info);
    }
    
    @Override
    public void output(StringCache cache, Map<String, Object> data)
    {
        Object target = data.get(varName);
        if (isFormat)
        {
            cache.append(format.format(varAccess.getValue(target)));
        }
        else
        {
            cache.append(varAccess.getValue(target));
        }
    }
    
    @Override
    public void addOutput(Output outPut)
    {
        throw new UnSupportException("");
    }
    
    @Override
    public void shirk()
    {
        // TODO Auto-generated method stub
        
    }
    
}
