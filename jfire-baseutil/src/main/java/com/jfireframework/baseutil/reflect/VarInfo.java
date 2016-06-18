package com.jfireframework.baseutil.reflect;

public class VarInfo
{
    private String   varChain;
    private Class<?> varType;
    private Class<?> rootType;
    
    public String getVarChain()
    {
        return varChain;
    }
    
    public void setVarChain(String varChain)
    {
        this.varChain = varChain;
    }
    
    public Class<?> getVarType()
    {
        return varType;
    }
    
    public void setVarType(Class<?> varType)
    {
        this.varType = varType;
    }
    
    public Class<?> getRootType()
    {
        return rootType;
    }
    
    public void setRootType(Class<?> rootType)
    {
        this.rootType = rootType;
    }
    
}
