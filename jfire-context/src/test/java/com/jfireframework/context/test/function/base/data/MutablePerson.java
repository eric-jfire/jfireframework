package com.jfireframework.context.test.function.base.data;

import javax.annotation.Resource;

@Resource(shareable = false)
public class MutablePerson
{
    private int    age;
    private String name;
    @Resource
    private House  home;
    
    public House getHome()
    {
        return home;
    }
    
    public void setHome(House home)
    {
        this.home = home;
    }
    
    public int getAge()
    {
        return age;
    }
    
    public void setAge(int age)
    {
        this.age = age;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
}
