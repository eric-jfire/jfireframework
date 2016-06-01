package com.jfireframework.context.test.function.initmethod;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Resource
public class Person
{
    private int    age;
    private String name;
    
    @PostConstruct
    public void preset()
    {
        setAge();
        setName();
    }
    
    public void setAge()
    {
        age = 23;
    }
    
    public void setName()
    {
        name = "林斌";
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setAge(int age)
    {
        this.age = age;
    }
    
    public int getAge()
    {
        return age;
    }
}
