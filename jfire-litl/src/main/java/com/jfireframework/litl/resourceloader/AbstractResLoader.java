package com.jfireframework.litl.resourceloader;

import java.util.concurrent.ConcurrentHashMap;
import com.jfireframework.litl.TplCenter;
import com.jfireframework.litl.template.Template;

public abstract class AbstractResLoader implements TplResLoader
{
    protected ConcurrentHashMap<String, Template> tplMap = new ConcurrentHashMap<String, Template>();
    
    @Override
    public Template loadTemplate(String path, TplCenter tplCenter)
    {
        Template template = tplMap.get(path);
        if (template == null)
        {
            synchronized (tplMap)
            {
                template = tplMap.get(path);
                if (template == null)
                {
                    template = buildTemplate(path, tplCenter);
                    tplMap.putIfAbsent(path, template);
                }
            }
        }
        return template;
    }
    
    protected abstract Template buildTemplate(String path, TplCenter tplCenter);
}
