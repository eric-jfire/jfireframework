package com.jfireframework.codejson;

public interface Json
{
    public boolean hasParentNode();
    
    public void setParentNode(Json json);
    
    public Json getParentNode();
}
