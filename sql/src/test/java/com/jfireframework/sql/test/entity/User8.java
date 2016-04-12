package com.jfireframework.sql.test.entity;

import com.jfireframework.sql.annotation.Id;
import com.jfireframework.sql.annotation.TableEntity;

@TableEntity(name = "user")
public class User8
{
    @Id
    private Integer userid;
    private int     age;
    private Float   weight;
                    
    public Float getWeight()
    {
        return weight;
    }
    
    public void setWeight(Float weight)
    {
        this.weight = weight;
    }
    
    public Integer getUserid()
    {
        return userid;
    }
    
    public void setUserid(Integer userid)
    {
        this.userid = userid;
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
