package com.jfireframework.litl.function.impl;

import java.util.Map;
import com.jfireframework.litl.TplCenter;
import com.jfireframework.litl.function.Function;
import com.jfireframework.litl.template.Template;

public class Include implements Function
{
    
    @Override
    public void call(Object[] params, Map<String, Object> data, StringBuilder builder, TplCenter tplCenter)
    {
        Template template = tplCenter.load((String) params[0]);
        String append = template.render(data);
        builder.append(append);
    }
    
}
