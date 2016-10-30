package com.jfireframework.boot;

import java.io.File;

public class BootConfig
{
    private int    port = 80;
    private String baseDir;
    private String appName;
    private String docBase;
    
    public static BootConfig newMavenEnv(String appName)
    {
        BootConfig config = new BootConfig();
        config.setBaseDir(new File("target/").getAbsolutePath());
        config.setDocBase(new File("src/main/webapp/").getAbsolutePath());
        config.setAppName(appName);
        return config;
    }
    
    public int getPort()
    {
        return port;
    }
    
    public void setPort(int port)
    {
        this.port = port;
    }
    
    public String getBaseDir()
    {
        return baseDir;
    }
    
    public void setBaseDir(String baseDir)
    {
        this.baseDir = baseDir;
    }
    
    public String getAppName()
    {
        return appName;
    }
    
    public void setAppName(String appName)
    {
        this.appName = appName;
    }
    
    public String getDocBase()
    {
        return docBase;
    }
    
    public void setDocBase(String docBase)
    {
        this.docBase = docBase;
    }
    
}
