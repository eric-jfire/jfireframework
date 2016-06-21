package com.jfireframework.litl;

import com.jfireframework.litl.resourceloader.TplResLoader;
import com.jfireframework.litl.template.Template;

public class TplCenter extends TempLateConfig
{
    private final TplResLoader tplResLoader;
    
    public TplCenter(TplResLoader tlResLoader)
    {
        this.tplResLoader = tlResLoader;
    }
    
    public TplCenter(TplResLoader tplResLoader, ClassLoader classLoader)
    {
        this.tplResLoader = tplResLoader;
    }
    
    public Template load(String name)
    {
        return tplResLoader.loadTemplate(name, this);
    }
    
    public String getRootPath()
    {
        return tplResLoader.getRootPath();
    }
    
}
