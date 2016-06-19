package com.jfireframework.litl.resourceloader;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import com.jfireframework.litl.TplCenter;
import com.jfireframework.litl.template.FileTemplate;
import com.jfireframework.litl.template.Template;

public class FileResLoader implements TlResLoader
{
    private ConcurrentHashMap<String, Template> tplMap = new ConcurrentHashMap<String, Template>();
    private final File                          root;
    
    public FileResLoader(File root)
    {
        this.root = root;
    }
    
    public FileResLoader(String path)
    {
        root = new File(path);
    }
    
    @Override
    public Template loadTemplate(String name, TplCenter tplCenter)
    {
        Template template = tplMap.get(name);
        if (template == null)
        {
            synchronized (tplMap)
            {
                template = tplMap.get(name);
                if (template == null)
                {
                    template = new FileTemplate(new File(root, name), tplCenter);
                    tplMap.putIfAbsent(name, template);
                }
            }
        }
        return template;
    }
    
}
