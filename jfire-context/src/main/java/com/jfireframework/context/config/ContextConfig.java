package com.jfireframework.context.config;

public class ContextConfig
{
    private String[]        packageNames = new String[0];
    
    private BeanInfo[]      beans        = new BeanInfo[0];
    
    private BeanAttribute[] beanConfigs  = new BeanAttribute[0];
    
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
    
    public BeanAttribute[] getBeanConfigs()
    {
        return beanConfigs;
    }
    
    public void setBeanConfigs(BeanAttribute[] beanConfigs)
    {
        this.beanConfigs = beanConfigs;
    }
    
}
