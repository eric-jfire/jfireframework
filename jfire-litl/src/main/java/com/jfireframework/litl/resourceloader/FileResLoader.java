package com.jfireframework.litl.resourceloader;

import java.io.File;
import com.jfireframework.litl.TplCenter;
import com.jfireframework.litl.template.Template;
import com.jfireframework.litl.template.impl.FileTemplate;

public class FileResLoader extends AbstractResLoader
{
    private final File root;
    
    public FileResLoader(File root)
    {
        this.root = root;
    }
    
    public FileResLoader(String path)
    {
        root = new File(path);
    }
    
    @Override
    protected Template buildTemplate(String path, TplCenter tplCenter)
    {
        return new FileTemplate(new File(root, path), path, tplCenter);
    }
    
    @Override
    public String getRootPath()
    {
        return root.getAbsolutePath();
    }

    
}
