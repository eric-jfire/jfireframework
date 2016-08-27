package com.jfireframework.mvc.newbinder.node;

public class StringValueNode implements ParamNode
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
