package com.jfireframework.litl;

import com.jfireframework.litl.resourceloader.TplResLoader;
import com.jfireframework.litl.template.Template;
import com.jfireframework.litl.tplrender.RenderBuilder;

public class TplCenter extends TempLateConfig
{
    private final TplResLoader  tplResLoader;
    private final RenderBuilder renderBuilder;
    
    public TplCenter(TplResLoader tlResLoader)
    {
        this.tplResLoader = tlResLoader;
        renderBuilder = new RenderBuilder(null);
    }
    
    public TplCenter(TplResLoader tplResLoader, ClassLoader classLoader)
    {
        this.tplResLoader = tplResLoader;
        renderBuilder = new RenderBuilder(classLoader);
    }
    
    public Template load(String name)
    {
        return tplResLoader.loadTemplate(name, this);
    }
    
    public String getRootPath()
    {
        return tplResLoader.getRootPath();
    }
    
    public RenderBuilder getBuilder()
    {
        return renderBuilder;
    }
    
}
