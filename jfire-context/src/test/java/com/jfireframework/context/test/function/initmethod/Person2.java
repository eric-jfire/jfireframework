package com.jfireframework.context.test.function.initmethod;

import javax.annotation.Resource;

@Resource(name = "p2")
public class Person2
{
    private int age;
    
    public void initage()
    {
        System.out.println("初始化");
        age = 12;
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
