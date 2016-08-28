package com.jfireframework.mvc.newbinder.node;

import java.util.ArrayList;

public class ArrayNode implements ParamNode
{
    private ArrayList<String> array = new ArrayList<String>();
    
    public void add(String value)
    {
        array.add(value);
    }
    
    public ArrayList<String> getArray()
    {
        return array;
    }
    
}
