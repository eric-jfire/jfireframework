package com.jfireframework.sql.test.entity;

import java.util.Date;
import com.jfireframework.sql.annotation.Column;
import com.jfireframework.sql.annotation.Id;
import com.jfireframework.sql.annotation.TableEntity;

@TableEntity(name = "user")
public class User4
{
    @Column(name = "userid")
    @Id
    private Integer id;
    private Long    age;
    private float   weight;
    @Column(name = "birthday")
    private Date    date;
                    
    public Long getAge()
    {
        return age;
    }
    
    public void setAge(Long age)
    {
        this.age = age;
    }
    
    public Integer getId()
    {
        return id;
    }
    
    public void setId(Integer id)
    {
        this.id = id;
    }
    
    public float getWeight()
    {
        return weight;
    }
    
    public void setWeight(float weight)
    {
        this.weight = weight;
    }
    
    public Date getDate()
    {
        return date;
    }
    
    public void setDate(Date date)
    {
        this.date = date;
    }
    
}
