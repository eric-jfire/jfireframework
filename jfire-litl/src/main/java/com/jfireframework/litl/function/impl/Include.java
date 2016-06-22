package com.jfireframework.litl.function.impl;

import java.util.Map;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.litl.function.Function;
import com.jfireframework.litl.template.LineInfo;
import com.jfireframework.litl.template.Template;

public class Include implements Function
{
    private Template template;
    
    @Override
    public void call(Map<String, Object> data, StringCache cache)
    {
        String append = template.render(data);
        cache.append(append);
    }
    
    @Override
    public void init(Object[] params, LineInfo lineInfo, Template template)
    {
        String path = (String) params[0];
        try
        {
            path = path.replace('/', '\\');
            this.template = template.load(path);
        }
        catch (Exception e)
        {
            throw new UnSupportException(StringUtil.format("找不到模板:{},请检查模板:{}的第{}行", path, template.getPath(), lineInfo.getLine()));
        }
    }
    
}
