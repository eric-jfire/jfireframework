package com.jfireframework.baseutil.data;

public class Person
{
    private String name = "linbin";
    private int    age;
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
