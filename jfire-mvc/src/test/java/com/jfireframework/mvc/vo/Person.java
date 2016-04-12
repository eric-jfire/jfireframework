package com.jfireframework.mvc.vo;

public class Person
{
    private String    name;
    private Integer   age;
    private Float     weight;
    private Integer[] ids = new Integer[5];
    
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
    
    public Integer[] getIds()
    {
        return ids;
    }
    
    public void setIds(Integer[] ids)
    {
        this.ids = ids;
    }
    
    public void setAge(Integer age)
    {
        this.age = age;
    }
    
    public void setWeight(Float weight)
    {
        this.weight = weight;
    }
    
}
