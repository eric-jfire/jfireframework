package com.jfireframework.litl.function.impl;

import java.util.Map;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.litl.function.Function;
import com.jfireframework.litl.template.Template;

public class Include implements Function
{
    
    @Override
    public void call(Object[] params, Map<String, Object> data, StringBuilder builder, Template template)
    {
        String path = (String) params[0];
        int line = (Integer) params[params.length - 1];
        try
        {
            path = path.replace('/', '\\');
            template = template.load(path);
        }
        catch (Exception e)
        {
            throw new UnSupportException(StringUtil.format("找不到模板:{},请检查模板:{}的第{}行", path, template.getPath(), line));
        }
        String append = template.render(data);
        builder.append(append);
    }
    
}
