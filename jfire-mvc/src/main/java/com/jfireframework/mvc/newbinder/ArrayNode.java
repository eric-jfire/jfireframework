package com.jfireframework.mvc.newbinder;

import java.util.ArrayList;

public class ArrayNode implements ParamTreeNode
{
    private ArrayList<String> array = new ArrayList<>();
    
    public void add(String value)
    {
        array.add(value);
    }
    
    public ArrayList<String> getArray()
    {
        return array;
    }
    
}
