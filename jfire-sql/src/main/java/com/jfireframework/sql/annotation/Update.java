package com.jfireframework.sql.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 该注解的接口方法表明该方法被调用的时候会发出该sql语句
 * 
 * @author 林斌（windfire@zailanghua.com）
 *         
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Update
{
    /**
     * 需要发出的sql语句
     * 
     * @return
     */
    public String sql();
    
    /**
     * 方法形参名称
     * 
     * @return
     */
    public String[] paramNames();
    
}
