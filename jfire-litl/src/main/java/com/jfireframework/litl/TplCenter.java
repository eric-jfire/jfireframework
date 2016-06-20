package com.jfireframework.litl;

import com.jfireframework.litl.resourceloader.TplResLoader;
import com.jfireframework.litl.template.Template;
import javassist.ClassPool;

public class TplCenter extends TempLateConfig
{
    private final TplResLoader tplResLoader;
    
    public TplCenter(TplResLoader tlResLoader)
    {
        ClassPool.doPruning = true;
        this.tplResLoader = tlResLoader;
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
