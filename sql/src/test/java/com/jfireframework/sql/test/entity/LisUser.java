package com.jfireframework.sql.test.entity;

import java.sql.Timestamp;
import com.jfireframework.sql.annotation.Column;
import com.jfireframework.sql.annotation.Id;
import com.jfireframework.sql.annotation.TableEntity;

@TableEntity(name = "user")
public class LisUser
{
    @Id
    @Column(name = "userid")
    private Integer   id;
    private Timestamp birthday;
                      
    public Integer getId()
    {
        return id;
    }
    
    public void setId(Integer id)
    {
        this.id = id;
    }
    
    public Timestamp getBirthday()
    {
        return birthday;
    }
    
    public void setBirthday(Timestamp birthday)
    {
        this.birthday = birthday;
    }
    
}
