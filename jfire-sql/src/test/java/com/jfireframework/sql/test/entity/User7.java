package com.jfireframework.sql.test.entity;

import java.util.Calendar;
import com.jfireframework.sql.annotation.Column;
import com.jfireframework.sql.annotation.Id;
import com.jfireframework.sql.annotation.TableEntity;

@TableEntity(name = "user")
public class User7
{
    @Id
    private Integer  userid;
    private long     age;
    private Double   weight;
    @Column(name = "birthday")
    private Calendar calendar;
    
    public Calendar getCalendar()
    {
        return calendar;
    }
    
    public void setCalendar(Calendar calendar)
    {
        this.calendar = calendar;
    }
    
    public Double getWeight()
    {
        return weight;
    }
    
    public void setWeight(Double weight)
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
    
    public long getAge()
    {
        return age;
    }
    
    public void setAge(long age)
    {
        this.age = age;
    }
    
}
