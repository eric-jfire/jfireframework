package com.jfireframework.litl.resourceloader;

import java.io.File;
import javax.servlet.ServletContext;
import com.jfireframework.litl.TplCenter;
import com.jfireframework.litl.template.Template;
import com.jfireframework.litl.template.impl.FileTemplate;

public class WebAppResLoader extends AbstractResLoader
{
    private final ServletContext servletContext;
    private final String         rootPath;
    
    public WebAppResLoader(ServletContext servletContext)
    {
        this.servletContext = servletContext;
        rootPath = servletContext.getRealPath("/");
    }
    
    @Override
    protected Template buildTemplate(String name, TplCenter tplCenter)
    {
        return new FileTemplate(new File(servletContext.getRealPath(name)), name, tplCenter);
    }
    
    @Override
    public String getRootPath()
    {
        return rootPath;
    }
    
}
