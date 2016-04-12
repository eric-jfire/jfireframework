package com.jfireframework.sql.test.entity;

import java.sql.Time;
import com.jfireframework.sql.annotation.Id;
import com.jfireframework.sql.annotation.TableEntity;

@TableEntity(name = "user")
public class User6
{
    @Id
    private Integer userid;
    private Time    time;
                    
    public Integer getUserid()
    {
        return userid;
    }
    
    public void setUserid(Integer userid)
    {
        this.userid = userid;
    }
    
    public Time getTime()
    {
        return time;
    }
    
    public void setTime(Time time)
    {
        this.time = time;
    }
    
}
