package com.jfireframework.sql.test.entity;

import java.sql.Time;
import java.sql.Timestamp;
import com.jfireframework.sql.annotation.Column;
import com.jfireframework.sql.annotation.Id;
import com.jfireframework.sql.annotation.TableEntity;

@TableEntity(name = "user")
public class User5
{
    @Id
    @Column(name = "userid")
    private Integer   id;
    private float     weight;
    @Column(name = "birthday")
    private Timestamp date;
    private Boolean   boy;
    @Column(saveIgnore = true)
    private Time      birthday;
    
    public Time getBirthday()
    {
        return birthday;
    }
    
    public void setBirthday(Time birthday)
    {
        this.birthday = birthday;
    }
    
    public Boolean getBoy()
    {
        return boy;
    }
    
    public void setBoy(Boolean boy)
    {
        this.boy = boy;
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
    
    public Timestamp getDate()
    {
        return date;
    }
    
    public void setDate(Timestamp date)
    {
        this.date = date;
    }
    
}
