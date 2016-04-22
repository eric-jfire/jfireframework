package com.jfireframework.codejson.function;

public interface Strategy
{
    public String getRename(String fieldName);
    
    public boolean containsRename(String fieldName);
}
