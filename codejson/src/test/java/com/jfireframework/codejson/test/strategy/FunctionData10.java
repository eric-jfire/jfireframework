package com.jfireframework.codejson.test.strategy;

import com.jfireframework.codejson.annotation.JsonRename;

public class FunctionData10
{
    @JsonRename("name2")
    private String name = "林斌";
    private int    age  = 23;
    
    public String getAddress()
    {
        return "天鹅湾";
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    @JsonRename("a")
    public int getAge()
    {
        return age;
    }
    
    public void setAge(int age)
    {
        this.age = age;
    }
    
}
