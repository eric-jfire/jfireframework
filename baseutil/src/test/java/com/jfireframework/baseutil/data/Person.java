package com.jfireframework.baseutil.data;

import com.jfireframework.baseutil.annotation.FieldOrder;

public class Person
{
    @FieldOrder(1)
    private String name = "linbin";
    @FieldOrder(2)
    private int    age;
    @FieldOrder(3)
    private float  weight;
    private Home   home;
                   
    public Home getHome()
    {
        return home;
    }
    
    public void setHome(Home home)
    {
        this.home = home;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public int getAge()
    {
        return age;
    }
    
    public void setAge(int age)
    {
        this.age = age;
    }
    
    public float getWeight()
    {
        return weight;
    }
    
    public void setWeight(float weight)
    {
        this.weight = weight;
    }
    
}
