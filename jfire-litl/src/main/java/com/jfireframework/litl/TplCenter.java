package com.jfireframework.litl;

import com.jfireframework.litl.resourceloader.TplResLoader;
import com.jfireframework.litl.template.Template;
import javassist.ClassPool;

public class TplCenter extends TempLateConfig
{
    private final TplResLoader tlResLoader;
    
    public TplCenter(TplResLoader tlResLoader)
    {
        ClassPool.doPruning = true;
        this.tlResLoader = tlResLoader;
    }
    
    public Template load(String name)
    {
        return tlResLoader.loadTemplate(name, this);
    }
}
