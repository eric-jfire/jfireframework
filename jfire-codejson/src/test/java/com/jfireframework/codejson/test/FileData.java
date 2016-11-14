package com.jfireframework.codejson.test;

import java.io.File;
import com.jfireframework.codejson.JsonTool;

public class FileData
{
    private File file;
    
    public File getFile()
    {
        return file;
    }
    
    public void setFile(File file)
    {
        this.file = file;
    }
    
    public static void main(String[] args)
    {
        FileData data = new FileData();
        data.setFile(new File("/ab"));
        System.out.println(JsonTool.write(data));
    }
}
