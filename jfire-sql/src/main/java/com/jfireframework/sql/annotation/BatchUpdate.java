package com.jfireframework.sql.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 该注解表明该接口的实现会发出对应的批量sql语句
 * 
 * @author 林斌（windfire@zailanghua.com）
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BatchUpdate
{
    /**
     * 需要发出的sql语句
     * 
     * @return
     */
    public String sql();
    
    /**
     * 方法的形参名称
     * 
     * @return
     */
    public String paramNames();
    
}
