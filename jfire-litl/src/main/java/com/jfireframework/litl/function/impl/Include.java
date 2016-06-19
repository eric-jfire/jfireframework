package com.jfireframework.litl.function.impl;

import java.util.Map;
import com.jfireframework.litl.function.Function;
import com.jfireframework.litl.template.Template;

public class Include implements Function
{
    
    @Override
    public void call(Object[] params, Map<String, Object> data, StringBuilder builder, Template template)
    {
        String path = (String) params[0];
        if (path.charAt(0) == '/')
        {
            template = template.getTplCenter().load(path);
            String append = template.render(data);
            builder.append(append);
        }
        else
        {
            
        }
        
    }
    
}
