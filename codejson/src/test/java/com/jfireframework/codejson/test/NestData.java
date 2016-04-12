package com.jfireframework.codejson.test;

public class NestData
{
    private String name;
    private int    age = 26;
    
    public boolean equals(Object entity)
    {
        if (entity instanceof NestData)
        {
            NestData target = (NestData) entity;
            if (name.equals(target.getName()) && age == target.getAge())
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
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
    
}
