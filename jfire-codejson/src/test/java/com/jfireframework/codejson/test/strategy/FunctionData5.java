package com.jfireframework.codejson.test.strategy;

import java.util.ArrayList;
import java.util.List;

public class FunctionData5
{
    private List<String>[] listArrays = new ArrayList[2];
    
    public FunctionData5()
    {
        List<String> list = new ArrayList<String>();
        list.add("dsads");
        list.add("dsadssdsasdas");
        listArrays[0] = list;
        list = new ArrayList<String>();
        list.add("ds1212s");
        list.add("d121212dsasdas");
        listArrays[1] = list;
    }
    
    public List<String>[] getListArrays()
    {
        return listArrays;
    }
    
    public void setListArrays(List<String>[] listArrays)
    {
        this.listArrays = listArrays;
    }
    
}
