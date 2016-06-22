package com.jfireframework.litl.output.impl;

import java.util.Map;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.litl.format.FormatRegister;
import com.jfireframework.litl.output.Output;
import com.jfireframework.litl.template.LineInfo;
import com.jfireframework.litl.varaccess.VarAccess;

public class VarOutput implements Output
{
    private final String     varName;
    private static VarAccess varAccess = new VarAccess();
    private final String     key;
    private String           pattern   = null;
    
    public VarOutput(String method, LineInfo info)
    {
        if (method.contains(","))
        {
            String[] spl = method.split(",");
            key = method;
            String[] tmp = spl[0].split("\\.");
            varName = tmp[0];
            pattern = spl[1];
        }
        else
        {
            
            key = method;
            String[] tmp = method.split("\\.");
            varName = tmp[0];
        }
    }
    
    @Override
    public void output(StringCache cache, Map<String, Object> data)
    {
        Object target = data.get(varName);
        if (pattern == null)
        {
            cache.append(varAccess.getValue(key, key, target, 0));
        }
        else
        {
            cache.append(FormatRegister.format(varAccess.getValue(key, key, target, 0), pattern));
        }
    }
    
    @Override
    public void addOutput(Output outPut)
    {
        throw new UnSupportException("");
    }
    
}
