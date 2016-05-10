package com.jfireframework.codejson.test.strategy;

import java.util.ArrayList;
import java.util.List;

public class FunctionData3
{
    private List<String> list = new ArrayList<String>();
    
    public FunctionData3()
    {
        list.add("hello1");
        list.add("hello2");
    }
    
    public List<String> getList()
    {
        return list;
    }
    
    public void setList(List<String> list)
    {
        this.list = list;
    }
    
}
