package com.jfireframework.litl.resourceloader;

import java.io.File;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.litl.TplCenter;
import com.jfireframework.litl.template.Template;
import com.jfireframework.litl.template.impl.FileTemplate;

public class WebAppResLoader extends AbstractResLoader
{
    private final String rootPath;
    
    public WebAppResLoader()
    {
        try
        {
            String path = WebAppResLoader.class.getClassLoader().getResource("").toURI().getPath();
            if (path == null)
            {
                throw new NullPointerException("Litl未能自动检测到WebRoot，请手工指定WebRoot路径");
            }
            rootPath = new File(path).getParentFile().getParentFile().getCanonicalPath();
        }
        catch (Exception e)
        {
            throw new UnSupportException("", e);
        }
        
    }
    
    @Override
    protected Template buildTemplate(String name, TplCenter tplCenter)
    {
        return new FileTemplate(new File(rootPath, name), name, tplCenter);
    }
    
    @Override
    public String getRootPath()
    {
        return rootPath;
    }
    
}
