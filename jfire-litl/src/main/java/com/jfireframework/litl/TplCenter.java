package com.jfireframework.litl;

import com.jfireframework.litl.resourceloader.TlResLoader;
import com.jfireframework.litl.template.Template;
import javassist.ClassPool;

public class TplCenter extends TempLateConfig
{
    private final TlResLoader tlResLoader;
    
    public TplCenter(TlResLoader tlResLoader)
    {
        ClassPool.doPruning = true;
        this.tlResLoader = tlResLoader;
    }
    
    public Template load(String name)
    {
        return tlResLoader.loadTemplate(name, this);
    }
}
