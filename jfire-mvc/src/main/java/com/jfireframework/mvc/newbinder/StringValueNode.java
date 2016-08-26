package com.jfireframework.mvc.newbinder;

public class StringValueNode implements ParamTreeNode
{
    private final String value;
    
    public StringValueNode(String value)
    {
        this.value = value;
    }
    
    public String getValue()
    {
        return value;
    }
}
