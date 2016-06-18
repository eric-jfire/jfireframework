package com.jfireframework.litl.function.impl;

import java.util.Map;
import com.jfireframework.litl.TplCenter;
import com.jfireframework.litl.TplRender;
import com.jfireframework.litl.function.Function;

public class Include implements Function
{
    
    @Override
    public void call(Object[] params, Map<String, Object> data, StringBuilder builder, TplCenter tplCenter)
    {
        TplRender render = tplCenter.get((String) params[0], data);
        String append = render.render(data);
        builder.append(append);
    }
    
}
