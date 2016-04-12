package com.jfireframework.sql.test.entity;

import com.jfireframework.sql.annotation.Column;
import com.jfireframework.sql.annotation.Id;
import com.jfireframework.sql.annotation.TableEntity;

@TableEntity(name = "home")
public class Home
{
    @Id
    @Column(name = "homeId")
    private Integer id;
    @Column(name = "home_name")
    private String  name;
}
