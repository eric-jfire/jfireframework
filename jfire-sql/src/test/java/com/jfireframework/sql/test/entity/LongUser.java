package com.jfireframework.sql.test.entity;

import com.jfireframework.sql.annotation.Column;
import com.jfireframework.sql.annotation.Id;
import com.jfireframework.sql.annotation.TableEntity;

@TableEntity(name = "user")
public class LongUser
{
    @Id
    @Column(name = "userid")
    private Integer id;
    private long    birthday;
    
    public Integer getId()
    {
        return id;
    }
    
    public void setId(Integer id)
    {
        this.id = id;
    }
    
    public long getBirthday()
    {
        return birthday;
    }
    
    public void setBirthday(long birthday)
    {
        this.birthday = birthday;
    }
    
}
