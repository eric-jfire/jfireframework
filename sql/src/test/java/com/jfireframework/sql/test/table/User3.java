package com.jfireframework.sql.test.table;

import com.jfireframework.sql.annotation.Column;
import com.jfireframework.sql.annotation.Id;
import com.jfireframework.sql.annotation.TableEntity;

@TableEntity(name = "user")
public class User3
{
    @Id
    @Column(name = "userid")
    private Integer id;
    @Column(name = "username")
    private String  name;
    private String  password;
    private Integer age;
    private String  birthday;
    private boolean boy;
    @Column(name = "boy", saveIgnore = true)
    private Boolean wboy;
    private double  weight;
    @Column(name = "weight", saveIgnore = true)
    private float   weight2;
                    
    public double getWeight()
    {
        return weight;
    }
    
    public void setWeight(double weight)
    {
        this.weight = weight;
    }
    
    public float getWeight2()
    {
        return weight2;
    }
    
    public void setWeight2(float weight2)
    {
        this.weight2 = weight2;
    }
    
    public Integer getId()
    {
        return id;
    }
    
    public void setId(Integer id)
    {
        this.id = id;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    public Integer getAge()
    {
        return age;
    }
    
    public void setAge(Integer age)
    {
        this.age = age;
    }
    
    public String getBirthday()
    {
        return birthday;
    }
    
    public void setBirthday(String birthday)
    {
        this.birthday = birthday;
    }
    
    public boolean isBoy()
    {
        return boy;
    }
    
    public void setBoy(boolean boy)
    {
        this.boy = boy;
    }
    
    public Boolean getWboy()
    {
        return wboy;
    }
    
    public void setWboy(Boolean wboy)
    {
        this.wboy = wboy;
    }
    
}
