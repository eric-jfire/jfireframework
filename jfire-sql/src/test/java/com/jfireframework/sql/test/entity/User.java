package com.jfireframework.sql.test.entity;

import com.jfireframework.sql.annotation.Column;
import com.jfireframework.sql.annotation.Id;
import com.jfireframework.sql.annotation.SqlEnumFieldUseInt;
import com.jfireframework.sql.annotation.TableEntity;

@TableEntity(name = "user")
public class User
{
    @SqlEnumFieldUseInt
    public static enum Enumint
    {
        a, b;
    }
    
    public static enum Enumstring
    {
        a, b;
    }
    
    @Id
    @Column(name = "userid")
    private Integer    id;
    @Column(name = "username")
    private String     name;
    private String     password;
    private Integer    age;
    private String     birthday;
    private boolean    boy;
    public static int  staticId   = 1;
    private Enumint    enumint    = Enumint.a;
    private Enumstring enumstring = Enumstring.a;
    
    public Enumint getEnumint()
    {
        return enumint;
    }
    
    public void setEnumint(Enumint enumint)
    {
        this.enumint = enumint;
    }
    
    public Enumstring getEnumstring()
    {
        return enumstring;
    }
    
    public void setEnumstring(Enumstring enumstring)
    {
        this.enumstring = enumstring;
    }
    
    public boolean isBoy()
    {
        return boy;
    }
    
    public void setBoy(boolean boy)
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
    
}
