package com.jfireframework.context.config;

public class ContextConfig
{
    private String[]   packageNames = new String[0];
    private BeanInfo[] beans        = new BeanInfo[0];
    private String[]   properties   = new String[0];
    
    public String[] getProperties()
    {
        return properties;
    }
    
    public void setProperties(String[] properties)
    {
        this.properties = properties;
    }
    
    public String[] getPackageNames()
    {
        return packageNames;
    }
    
    public void setPackageNames(String[] packageNames)
    {
        this.packageNames = packageNames;
    }
    
    public BeanInfo[] getBeans()
    {
        return beans;
    }
    
    public void setBeans(BeanInfo[] beans)
    {
        this.beans = beans;
    }
    
}
