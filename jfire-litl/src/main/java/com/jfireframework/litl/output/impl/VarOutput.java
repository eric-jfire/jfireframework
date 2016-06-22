package com.jfireframework.litl.output.impl;

import java.util.Map;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.litl.format.Format;
import com.jfireframework.litl.format.impl.DateFormat;
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
        if (method.contains(","))
        {
            String[] spl = method.split(",");
            String[] tmp = spl[0].split("\\.");
            varName = tmp[0];
            String pattern = spl[1];
            if (pattern.charAt(0) == '"')
            {
                format = new DateFormat(pattern.substring(1, pattern.length() - 1).trim());
            }
            else if (pattern.startsWith("dateFormat="))
            {
                pattern = pattern.substring(11, pattern.length() - 1).trim();
                format = new DateFormat(pattern);
            }
            else if (pattern.startsWith("numberFormat="))
            {
                pattern = pattern.substring(13, pattern.length() - 1).trim();
                format = new DateFormat(pattern);
            }
            else
            {
                throw new UnSupportException(StringUtil.format("无法识别的格式化类型，请检查模板:{}的第{}行", template.getPath(), info.getLine()));
            }
            isFormat = true;
        }
        else
        {
            String[] tmp = method.split("\\.");
            varName = tmp[0];
            isFormat = false;
        }
        varAccess = new VarAccess(template.getPath(), varName, info);
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
