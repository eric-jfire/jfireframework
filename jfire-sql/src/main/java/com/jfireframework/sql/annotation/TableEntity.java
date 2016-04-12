package com.jfireframework.sql.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 该注解表明该类是一个数据库表的映射类
 * 
 * @author 林斌（windfire@zailanghua.com）
 *         
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TableEntity
{
    public String name();
    
}
