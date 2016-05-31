package com.jfireframework.context.test.function.base.data;

import javax.annotation.Resource;

@Resource
public class ImmutablePerson
{
    private int      age;
    private String   name;
    @Resource
    private House    home;
    private Boolean  boy;
    private String[] arrays;
    
    public String[] getArrays()
    {
        return arrays;
    }
    
    public void setArrays(String[] arrays)
    {
        this.arrays = arrays;
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
    
    public House getHome()
    {
        return home;
    }
    
    public void setHome(House home)
    {
        this.home = home;
    }
    
    public Boolean getBoy()
    {
        return boy;
    }
    
    public void setBoy(Boolean boy)
    {
        this.boy = boy;
    }
    
}
